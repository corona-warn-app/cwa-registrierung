package com.tsystems.mms.cwa.registration.cancellation.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "cancellation_jobs")
public class Job {
    @Id
    private String uuid = UUID.randomUUID().toString();
    private String filename;
    private String partnerType;
    private LocalDateTime created;
    private String bcc;
    private String additionalAttachment;
    private boolean cancelInPortal;
    private String subject;
    private boolean sendEmail;

    @OneToMany(mappedBy = "job")
    private List<JobEntry> entries = new ArrayList<>();
}
