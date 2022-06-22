package com.tsystems.mms.cwa.registration.cancellation.domain;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "cancellation_job_entries")
public class JobEntry {

    @Id
    private String uuid = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "job_uuid")
    @CsvIgnore
    private Job job;

    @CsvBindByPosition(position = 0)
    @CsvBindByName(column = "Partner-Nr.")
    private String partnerId;

    @CsvBindByPosition(position = 1)
    @CsvBindByName(column = "E-Mail")
    private String receiver;

    private String attachmentFilename;

    @CsvBindByPosition(position = 2)
    @CsvBindByName(column = "Created")
    private LocalDateTime created = LocalDateTime.now();

    @CsvBindByPosition(position = 3)
    @CsvBindByName(column = "Sent")
    private LocalDateTime sent;

    @CsvBindByPosition(position = 4)
    @CsvBindByName(column = "Errors")
    private String message;
}
