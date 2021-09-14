package com.tsystems.mms.cwa.registration.registration;

import com.tsystems.mms.cwa.registration.model.Address;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Embedded;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * DTO class for test center partner.
 *
 * @author Robin Lutter (Robin.Lutter@T-Systems.com)
 */
@Data
public class PartnerForm {

    @NotEmpty(message="Bitte geben Sie den Namen der Teststelle/des Betreibers an!")
    @Size(max=100)
    private String name;

    @NotEmpty(message="Bitte geben Sie den Namen des Ansprechpartners an!")
    @Size(max=100)
    private String contact;

    @NotEmpty(message = "Bitte geben Sie eine Emailadresse an!")
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message="Die angegebene Emailadresse ist ungültig!")
    @Size(max=255)
    private String email;

    @NotEmpty(message = "Bitte geben Sie eine Telefonnummer an!")
    @Pattern(regexp ="^[0-9]*\\/*(\\+49)*[ ]*(\\([0-9]+\\))*([ ]*(-|–)*[ ]*[0-9]+)*$", message="Die eingegebene Telefonnnumer ist ungültig.")
    @Size(max=100)
    private String phone;

    @Embedded
    @NotNull
    @Valid
    private Address address;

    /**
     * (optional) number of owned test center, could be more than registered.
     */
    @Pattern(regexp = "[0-9]*", message = "Ist keine gültige Zahl")
    private String count;

    @Pattern(regexp = "[0-9]*", message = "Ist keine gültige Zahl")
    private String estimatedCapacity;

    /**
     * if <code>hasSoftware</code> is true, if partner is software provider.
     */
    private String selectedSwSolution;
    private boolean legalisationProof;
    private Boolean rat;
    private LocalDateTime created;

    public PartnerForm() {
        address = new Address();
    }
}
