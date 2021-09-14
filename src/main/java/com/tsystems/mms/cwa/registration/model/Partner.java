package com.tsystems.mms.cwa.registration.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvRecurse;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Data model class for test center operator.
 *
 * @author Robin Lutter (Robin.Lutter@T-Systems.com)
 */
@Entity
@Table(name="partners")
@Data
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "partners_id_seq")
    @SequenceGenerator(name = "partners_id_seq", allocationSize = 1)
    private Long id;

    @CsvBindByName(column = "Partner/Betreiber")
    @CsvBindByPosition(position = 0)
    private String name;

    @CsvBindByName(column = "Ansprechpartner")
    @CsvBindByPosition(position = 4)
    private String contact;

    @Column(unique = true)
    @CsvBindByName(column = "Emailadresse")
    @CsvBindByPosition(position = 5)
    private String email;

    @CsvBindByPosition(position = 6)
    @CsvBindByName(column = "Telefonnummer")
    private String phone;

    @Embedded
    @CsvRecurse
    private Address address;
    /**
     * (optional) number of owned test center, could be more than registered.
     */
    @CsvBindByPosition(position = 7)
    @CsvBindByName(column = "Testzentren")
    private int count;

    /**
     * if <code>hasSoftware</code> is true, if partner is software provider.
     */
    @CsvBindByPosition(position = 9)
    @CsvBindByName(column = "Softwarelösung")
    private String softwareSolution;

    /**
     *
     */
    private boolean legalisationProof;

    @CsvBindByPosition(position = 11)
    private Boolean rat;

    @CsvBindByPosition(position = 8)
    @CsvBindByName(column = "Geschätzte Tests/d")
    private int estimatedCapacity;

    @CsvBindByPosition(position = 12)
    @CsvBindByName(column = "Vertragstyp")
    private transient String contractType = "Portal";

    @CsvBindByPosition(position = 13)
    @CsvDate(value = "dd.MM.yyyy hh:mm:ss")
    @CsvBindByName(column = "Erstellungszeitpunkt")
    private LocalDateTime created;

    public Partner() {
        address = new Address();
    }

    @PrePersist
    protected void onCreate() {
        this.setCreated(LocalDateTime.now());
    }
}
