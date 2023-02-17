package com.tsystems.mms.cwa.registration.cancellation.application;

import com.amazonaws.services.s3.AmazonS3;
import com.tsystems.mms.cwa.registration.cancellation.CancellationController;
import com.tsystems.mms.cwa.registration.cancellation.adapter.mail.MailService;
import com.tsystems.mms.cwa.registration.cancellation.adapter.quicktest.QuicktestPortalService;
import com.tsystems.mms.cwa.registration.cancellation.domain.Job;
import com.tsystems.mms.cwa.registration.cancellation.domain.JobEntry;
import com.tsystems.mms.cwa.registration.cancellation.domain.JobEntryRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CancellationsService {
    private final Logger log = LoggerFactory.getLogger(CancellationsService.class);
    private final Map<String, SenderThread> senderThreads = new HashMap<>();

    private final JobEntryRepository jobEntryRepository;
    private final MailService mailService;
    private final QuicktestPortalService quicktestPortalService;

    private final AmazonS3 s3Client;

    @Value("${obs.bucket-name}")
    private String bucketName;


    public CancellationsService(JobEntryRepository jobEntryRepository, MailService mailService, QuicktestPortalService quicktestPortalService, AmazonS3 s3Client) {
        this.jobEntryRepository = jobEntryRepository;
        this.mailService = mailService;
        this.quicktestPortalService = quicktestPortalService;
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

        Map<String, File> attachments = new HashMap<>();
        try {
            if (jobEntry.getJob().isSendEmail()) {
                var attachmentFile = File.createTempFile("attachment", ".pdf");
                FileUtils.copyInputStreamToFile(blob.getObjectContent(), attachmentFile);
                attachments.put(jobEntry.getAttachmentFilename(), attachmentFile);

                if (StringUtils.isNotEmpty(jobEntry.getJob().getAdditionalAttachment())) {
                    var additionalAttachmentFile = File.createTempFile("attachment", ".pdf");
                    var additionalAttachmentBlob = s3Client.getObject(bucketName, jobEntry.getJob().getAdditionalAttachment());
                    if (additionalAttachmentBlob == null) {
                        throw new IllegalStateException("Additional attachment to found");
                    }
                    FileUtils.copyInputStreamToFile(additionalAttachmentBlob.getObjectContent(), additionalAttachmentFile);
                    attachments.put(jobEntry.getJob().getAdditionalAttachment(), additionalAttachmentFile);
                }

                mailService.sendMail(
                        jobEntry.getReceiver(),
                        jobEntry.getJob().getBcc(),
                        jobEntry.getJob().getSubject()
                                .replace("{{partnerID}}", jobEntry.getPartnerId()),
                        body,
                        attachments);
            }

            if (jobEntry.getJob().isCancelInPortal()) {
                try {
                    jobEntry.setFinalDeletionResponse(quicktestPortalService.cancelAccount(
                            jobEntry.getPartnerId(),
                            jobEntry.getFinalDeletionRequest()
                    ));
                } catch (Exception e) {
                    jobEntry.setMessage("Cancellation: " + e.getMessage());
                }
            }

        } finally {
            for (File attachment : attachments.values()) {
                if (!attachment.delete()) {
                    log.warn("Error deleting attachment: {}", attachment);
                }
            }
        }
    }

    public boolean isJobRunning(String jobUuid) {
        return senderThreads.containsKey(jobUuid) && senderThreads.get(jobUuid).isRunning();
    }
}
