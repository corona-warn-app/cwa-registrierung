package com.tsystems.mms.cwa.registration.cancellation.application;

import com.amazonaws.services.s3.model.Bucket;
import com.tsystems.mms.cwa.registration.cancellation.domain.Job;
import com.tsystems.mms.cwa.registration.cancellation.domain.JobEntry;
import com.tsystems.mms.cwa.registration.cancellation.domain.JobEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CancellationsService {
    private final Logger log = LoggerFactory.getLogger(CancellationsService.class);
    private final Map<String, SenderThread> senderThreads = new HashMap<>();

    private final JobEntryRepository jobEntryRepository;
    private final MailService mailService;

    private final Bucket bucket;

    public CancellationsService(JobEntryRepository jobEntryRepository, MailService mailService, Bucket bucket) {
        this.jobEntryRepository = jobEntryRepository;
        this.mailService = mailService;
        this.bucket = bucket;
    }

    public void startJob(Job job) {
        final var runner = new SenderThread(job, jobEntryRepository, this);
        senderThreads.put(job.getUuid(), runner);
        runner.start();
    }

    public void processEntry(JobEntry jobEntry) {
        log.info("Processing log entry: receiver={}", jobEntry.getReceiver());

    }

    public boolean isJobRunning(String jobUuid) {
        return senderThreads.containsKey(jobUuid) && senderThreads.get(jobUuid).isRunning();
    }
}
