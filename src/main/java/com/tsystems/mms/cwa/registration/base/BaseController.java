package com.tsystems.mms.cwa.registration.base;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BaseController {

    @GetMapping("/impressum")
    public String impressum() {
        return "impressum";
    }

    @GetMapping("/datenschtutz")
    public String privacy() {
        return "privacy";
    }
}
