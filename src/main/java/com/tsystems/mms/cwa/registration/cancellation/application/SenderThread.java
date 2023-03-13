package com.tsystems.mms.cwa.registration.cancellation.application;

import com.tsystems.mms.cwa.registration.cancellation.domain.Job;
import com.tsystems.mms.cwa.registration.cancellation.domain.JobEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public class SenderThread {
    private final Logger log = LoggerFactory.getLogger(SenderThread.class);

    private final Job job;
    private final JobEntryRepository jobEntryRepository;
    private final CancellationsService cancellationsService;
    private boolean running = true;

    @Value("${email.delay}")
    private int mailDelay;

    public SenderThread(Job job, JobEntryRepository jobEntryRepository, CancellationsService cancellationsService) {
        this.job = job;
        this.jobEntryRepository = jobEntryRepository;
        this.cancellationsService = cancellationsService;
    }

    public void start() {
        new Thread(() -> {
            log.info("Starting job {}", job.getUuid());
            while (running) {
                final var entry = jobEntryRepository.findPendingEntry(
                        job.getUuid(),
                        Pageable.ofSize(1)
                ).stream().findFirst().orElse(null);
                if (entry == null) {
                    log.info("No pending entry found: job={}", job.getUuid());
                    break;
                }

                long startTimestamp = System.currentTimeMillis();
                try {
                    cancellationsService.processEntry(entry);
                    entry.setSent(LocalDateTime.now());
                    jobEntryRepository.save(entry);

                } catch (Exception e) {
                    log.error("Error processing job entry", e);
                    entry.setMessage(e.getMessage());
                    jobEntryRepository.save(entry);
                } finally {
                    long stopTimestamp = System.currentTimeMillis();
                    // Hacky, but totally ok for now
                    try {
                        Thread.sleep(Math.max(0, mailDelay - (stopTimestamp - startTimestamp)));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            log.info("Job {} stopped", job.getUuid());
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
