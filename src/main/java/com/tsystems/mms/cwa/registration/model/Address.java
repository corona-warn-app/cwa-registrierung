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

import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Entity class for storing address information.
 *
 * @author Robin Lutter (Robin.Lutter@T-Systems.com)
 */
@Embeddable
@Data
public class Address {

    @Size(max = 100)
    @NotEmpty(message = "Bitte geben Sie die Straße ein!")
    @CsvBindByPosition(position = 1)
    @CsvBindByName(column = "Street")
    private String street;

    @Size(max = 100)
    @NotEmpty(message = "Bitte geben Sie die Hausnummer ein!")
    @CsvBindByPosition(position = 2)
    @CsvBindByName(column = "House number")
    private String number;

    @NotEmpty(message = "Bitte geben Sie die Postleitzahl ein!")
    @Pattern(regexp = "^[0-9]{5}$", message = "Die angegebene Postleitzahl ist ungültig!")
    @CsvBindByPosition(position = 3)
    @CsvBindByName(column = "ZIP code")
    private String postalCode;

    @Size(max = 100)
    @NotEmpty(message = "Bitte geben Sie den Ort ein!")
    @CsvBindByPosition(position = 4)
    @CsvBindByName(column = "City")
    private String city;
}
