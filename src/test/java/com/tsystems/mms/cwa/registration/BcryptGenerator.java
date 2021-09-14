package com.tsystems.mms.cwa.registration;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * TODO: what am I doing?
 *
 * @author Robin Lutter (Robin.Lutter@T-Systems.com)
 */
public class BcryptGenerator {
    public static void main(String[] args) {

        int i = 0;
        while (i < 5) {
            String password = "top_secret";
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);

            System.out.println(hashedPassword);
            i++;
        }
    }
}

