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
