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
