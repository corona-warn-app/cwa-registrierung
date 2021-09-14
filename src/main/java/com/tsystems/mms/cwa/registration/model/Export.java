package com.tsystems.mms.cwa.registration.model;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO: what am I doing?
 *
 * @author Robin Lutter (Robin.Lutter@T-Systems.com)
 */
@Entity
@Table(name = "exports")
@Data
public class Export {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exports_id_seq")
    @SequenceGenerator(name = "exports_id_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private LocalDateTime exportTime;

    @NotNull
    private String issuedBy;

    public Export(String issuedBy) {
        this.issuedBy = issuedBy;
        this.exportTime = LocalDateTime.now();
    }

    public Export() {

    }
}
