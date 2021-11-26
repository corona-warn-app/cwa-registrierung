package com.tsystems.mms.cwa.registration.cancellation;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CancellationForm {

    @NotEmpty(message = "Bitte geben Sie Ihre Emailadresse an!")
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message="Die angegebene Emailadresse ist ungültig!")
    @Size(max=255)
    private String email;
}
