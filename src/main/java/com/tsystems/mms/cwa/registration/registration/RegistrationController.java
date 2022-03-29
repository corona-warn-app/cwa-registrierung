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

package com.tsystems.mms.cwa.registration.registration;

import com.tsystems.mms.cwa.registration.model.Partner;
import com.tsystems.mms.cwa.registration.service.RegistrationService;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * Webcontroller for handling web requests.
 *
 * @author Robin Lutter (Robin.Lutter@T-Systems.com)
 */
@Controller
public class RegistrationController {

    private final static String INDEX = "index";
    private final static String RESULT = "result";

    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final ModelMapper modelMapper = new ModelMapper();

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;

        TypeMap<Partner, PartnerForm> typeMap = modelMapper.createTypeMap(Partner.class, PartnerForm.class);
    }

    @GetMapping("/")
    public String viewIndex(PartnerForm partnerForm) {
        return INDEX;
    }

    @PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String register(@RequestParam(value = "files", required = false) MultipartFile[] files,
                           @Valid PartnerForm partnerForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            //yet another hack, caused by the submit action
            partnerForm.setLegalisationProof(false);
            return INDEX;
        }
        Partner partner = new ModelMapper().map(partnerForm, Partner.class);
        try {
            registrationService.createPartner(partner);
            modelMapper.map(partner, partnerForm);
            return RESULT;
        } catch (javax.validation.ConstraintViolationException e) {
            var fieldError = new FieldError("partner", "legalisationProof", e.getLocalizedMessage());
            bindingResult.addError(fieldError);
            return INDEX;
        } catch (DataIntegrityViolationException exception) {
            if (exception.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException e = (ConstraintViolationException) exception.getCause();
                logger.debug("got ConstraintViolation: {}", e.getLocalizedMessage());
                String field = e.getConstraintName().replace("_key", "").replace("_", ".");
                String sqlErrorMessage = e.getSQLException().getLocalizedMessage();
                if ("23505".equals(e.getSQLState()) && "partners.email".equals(field)) {
                    var fieldError = new FieldError(
                            field.substring(0, field.indexOf('.') - 1),
                            field.substring(field.indexOf('.') + 1),
                            "Die E-Mail Adresse ist bereits registriert.");
                    bindingResult.addError(fieldError);
                } else {
                    var fieldError = new FieldError(field.substring(0, field.indexOf('.') - 1), field.substring(field.indexOf('.') + 1),
                            sqlErrorMessage.substring(sqlErrorMessage.indexOf("Detail: ")));
                    bindingResult.addError(fieldError);
                }
            } else {
                logger.error("got exception:", exception);
                bindingResult.addError(new ObjectError("partners", "Es ist ein Fehler aufgetreten, bitte versuchen Sie es später erneut."));
            }
        }
        return INDEX;
    }
}
