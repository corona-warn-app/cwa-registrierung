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
    @CsvBindByName(column = "Straße")
    private String street;

    @Size(max = 100)
    @NotEmpty(message = "Bitte geben Sie die Hausnummer ein!")
    @CsvBindByPosition(position = 2)
    @CsvBindByName(column = "Hausnummer")
    private String number;

    @NotEmpty(message = "Bitte geben Sie die Postleitzahl ein!")
    @Pattern(regexp = "^[0-9]{5}$", message = "Die angegebene Postleitzahl ist ungültig!")
    @CsvBindByPosition(position = 3)
    @CsvBindByName(column = "PLZ")
    private String postalCode;

    @Size(max = 100)
    @NotEmpty(message = "Bitte geben Sie den Ort ein!")
    @CsvBindByPosition(position = 4)
    @CsvBindByName(column = "Ort")
    private String city;
}
