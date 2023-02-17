package com.tsystems.mms.cwa.registration.cancellation.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cancellation_job_summaries")
public class JobSummary {

    @Id
    private String uuid;
    private String filename;
    private String partnerType;
    private String bcc;
    private String subject;
    private LocalDateTime created;
    private int entries;
    private int sent;
    private int errors;
    private String additionalAttachment;
    private boolean cancelInPortal;
    private boolean sendEmail;
}
