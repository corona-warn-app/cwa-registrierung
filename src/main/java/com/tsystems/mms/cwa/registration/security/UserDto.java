package com.tsystems.mms.cwa.registration.security;

import lombok.Getter;
import lombok.Setter;

/**
 *
 *
 * @author Robin Lutter (Robin.Lutter@T-Systems.com)
 */
@Getter
@Setter
public class UserDto {
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private int statusCode;
    private String status;
}
