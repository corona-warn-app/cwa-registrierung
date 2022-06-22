package com.tsystems.mms.cwa.registration.cancellation.application;

import com.tsystems.mms.cwa.registration.cancellation.domain.Job;
import com.tsystems.mms.cwa.registration.cancellation.domain.JobEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class SenderThread {
    private final Logger log = LoggerFactory.getLogger(SenderThread.class);

    private final Job job;
    private final JobEntryRepository jobEntryRepository;
    private final CancellationsService cancellationsService;
    private boolean running = true;

    public SenderThread(Job job, JobEntryRepository jobEntryRepository, CancellationsService cancellationsService) {
        this.job = job;
        this.jobEntryRepository = jobEntryRepository;
        this.cancellationsService = cancellationsService;
    }

    public void start() {
        new Thread(() -> {
            while (running) {
                try {
                    final var entry = jobEntryRepository.findPendingEntry(job.getUuid());
                    if (entry == null) {
                        log.info("No pending entry found: job={}", job.getUuid());
                        break;
                    }
                    cancellationsService.processEntry(entry);
                    entry.setSent(LocalDateTime.now());
                    jobEntryRepository.save(entry);

                    // Hacky, but totally ok for now
                    Thread.sleep(2000);
                } catch (Exception e) {
                    log.error("Error processing job entry", e);
                }
            }
            running = false;
        }).start();
    }

    public void cancel() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
