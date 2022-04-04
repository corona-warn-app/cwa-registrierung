package com.tsystems.mms.cwa.registration.cancellation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CancellationController {

    @GetMapping("/cancellation")
    public String cancellation() {
        return "cancellation";
    }
}
