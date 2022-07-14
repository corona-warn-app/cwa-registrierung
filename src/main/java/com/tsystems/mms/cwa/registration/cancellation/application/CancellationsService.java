package com.tsystems.mms.cwa.registration.cancellation.application;

import com.amazonaws.services.s3.AmazonS3;
import com.tsystems.mms.cwa.registration.cancellation.CancellationController;
import com.tsystems.mms.cwa.registration.cancellation.adapter.mail.MailService;
import com.tsystems.mms.cwa.registration.cancellation.adapter.otc.OtcObsClient;
import com.tsystems.mms.cwa.registration.cancellation.domain.Job;
import com.tsystems.mms.cwa.registration.cancellation.domain.JobEntry;
import com.tsystems.mms.cwa.registration.cancellation.domain.JobEntryRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class CancellationsService {
    private final Logger log = LoggerFactory.getLogger(CancellationsService.class);
    private final Map<String, SenderThread> senderThreads = new HashMap<>();

    private final JobEntryRepository jobEntryRepository;
    private final MailService mailService;

    private final AmazonS3 s3Client;

    @Value("${obs.bucket-name}")
    private String bucketName;


    public CancellationsService(JobEntryRepository jobEntryRepository, MailService mailService, AmazonS3 s3Client) {
        this.jobEntryRepository = jobEntryRepository;
        this.mailService = mailService;
        this.s3Client = s3Client;
    }

    public void startJob(Job job) {
        final var runner = new SenderThread(job, jobEntryRepository, this);
        senderThreads.put(job.getUuid(), runner);
        runner.start();
    }

    public void stopJob(String jobId) {
        if (senderThreads.containsKey(jobId)) {
            senderThreads.get(jobId).cancel();
        }
    }

    public void processEntry(JobEntry jobEntry) throws IOException, MessagingException {
        log.info("Processing log entry: receiver={}", jobEntry.getReceiver());

        String body;
        try (InputStream inputStream = CancellationController.class.getResourceAsStream(String.format("/mail/cancellation.%s.html", jobEntry.getJob().getPartnerType()))) {
            if (inputStream == null) {
                throw new IOException("template not found");
            }
            body = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }

        var blob = s3Client.getObject(bucketName, jobEntry.getAttachmentFilename());
        if (blob == null) {
            throw new IllegalStateException("Attachment to found");
        }

        var tmpFile = new File(jobEntry.getAttachmentFilename());
        try {
            FileUtils.copyInputStreamToFile(blob.getObjectContent(), tmpFile);
            mailService.sendMail(
                    jobEntry.getReceiver(),
                    "Ihr Vertragsverhältnis zur Anbindung an die Corona Warn App",
                    body,
                    tmpFile);
        } finally {
            if (!tmpFile.delete()) {
                log.warn("Could not delete {}", tmpFile.getName());
            }
        }
    }

    public boolean isJobRunning(String jobUuid) {
        return senderThreads.containsKey(jobUuid) && senderThreads.get(jobUuid).isRunning();
    }
}
