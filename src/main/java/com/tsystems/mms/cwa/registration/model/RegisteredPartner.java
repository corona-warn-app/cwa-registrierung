package com.tsystems.mms.cwa.registration.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "registered_partners")
public class RegisteredPartner {

    @Id
    private String id;

    @CsvBindByName(column = "Partner")
    @CsvBindByPosition(position = 0)
    private String partnerNr;

    @CsvBindByName(column = "E-Mail")
    @CsvBindByPosition(position = 1)
    private String email;
    private String token;

    @CsvBindByName(column = "Approved")
    @CsvBindByPosition(position = 2)
    private LocalDateTime approved;

    private LocalDateTime exported;
}
