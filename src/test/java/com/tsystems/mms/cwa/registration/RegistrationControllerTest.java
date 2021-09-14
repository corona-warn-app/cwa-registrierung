package com.tsystems.mms.cwa.registration;

import java.nio.charset.StandardCharsets;

import com.tsystems.mms.cwa.registration.registration.RegistrationController;
import com.tsystems.mms.cwa.registration.service.RegistrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Test class for RegistrationController
 *
 * @author Robin Lutter (Robin.Lutter@T-Systems.com)
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class RegistrationControllerTest {

    @MockBean
    private RegistrationService registrationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void registerOk() throws Exception {
        MediaType textHtmlUtf8 = new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8);
        MultiValueMap<String, String> testcenter = createValidPartner();
        mockMvc.perform(MockMvcRequestBuilders.multipart("/new").params(testcenter))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(textHtmlUtf8));
    }

    @Test
    public void registerValidationError() throws Exception {
        MediaType textHtmlUtf8 = new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8);
        MultiValueMap<String, String> testcenter = createValidPartner();
        testcenter.remove("email");
        mockMvc.perform(MockMvcRequestBuilders.multipart("/new").params(testcenter))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(textHtmlUtf8));
    }

    private MultiValueMap<String, String> createValidPartner() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("name", "Testcenter");
        form.add("email", "nobody@nowhere.org");
        form.add("phone", "0351-0");

        form.add("address.postalCode", "01127");
        form.add("address.streetNo", "Unter dem Blauen Wunder 1b");
        form.add("address.city", "Dresden");

        form.add("count","42");


        form.add("isSoftwareProvider", "true");
        form.add("legalisationProof", "true");

        return form;
    }
}