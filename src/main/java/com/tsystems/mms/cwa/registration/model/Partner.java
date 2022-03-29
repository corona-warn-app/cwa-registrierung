/*
 * Corona-Warn-App / cwa-map-registrierung
 *
 * (C) 2020, T-Systems International GmbH
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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

    @CsvBindByName(column = "Partner/operator name")
    @CsvBindByPosition(position = 0)
    private String name;

    @CsvBindByName(column = "Contact person")
    @CsvBindByPosition(position = 4)
    private String contact;

    @Column(unique = true)
    @CsvBindByName(column = "Email address")
    @CsvBindByPosition(position = 5)
    private String email;

    @CsvBindByPosition(position = 6)
    @CsvBindByName(column = "Telephone")
    private String phone;

    @Embedded
    @CsvRecurse
    private Address address;
    /**
     * (optional) number of owned test center, could be more than registered.
     */
    @CsvBindByPosition(position = 7)
    @CsvBindByName(column = "test centres")
    private int count;

    /**
     * if <code>hasSoftware</code> is true, if partner is software provider.
     */
    @CsvBindByPosition(position = 9)
    @CsvBindByName(column = "Software solution")
    private String softwareSolution;

    /**
     *
     */
    private boolean legalisationProof;

    @CsvBindByPosition(position = 11)
    private Boolean rat;

    @CsvBindByPosition(position = 8)
    @CsvBindByName(column = "Estimated test/day")
    private int estimatedCapacity;

    @CsvBindByPosition(position = 12)
    @CsvBindByName(column = "Contract type")
    private transient String contractType = "Portal";

    @CsvBindByPosition(position = 13)
    @CsvDate(value = "dd.MM.yyyy hh:mm:ss")
    @CsvBindByName(column = "Created on")
    private LocalDateTime created;

    public Partner() {
        address = new Address();
    }

    @PrePersist
    protected void onCreate() {
        this.setCreated(LocalDateTime.now());
    }
}
