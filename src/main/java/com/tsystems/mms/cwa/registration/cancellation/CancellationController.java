package com.tsystems.mms.cwa.registration.cancellation;

import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.tsystems.mms.cwa.registration.export.EscapingCsvWriter;
import com.tsystems.mms.cwa.registration.export.HeaderColumnNameWithPositionMappingStrategy;
import com.tsystems.mms.cwa.registration.model.RegisteredPartner;
import com.tsystems.mms.cwa.registration.repository.RegisteredPartnerRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Controller
public class CancellationController {
    @Value("${config.host}")
    private String host;

    @Value("${email.from}")
    private String emailFrom;

    @Value("${email.smtp-host}")
    private String emailSmtpHost;

    @Value("${email.smtp-port}")
    private String emailSmtpPort;

    @Value("${email.smtp-user}")
    private String emailSmtpUser;

    @Value("${email.smtp-password}")
    private String emailSmtpPassword;

    private final RegisteredPartnerRepository registeredPartnerRepository;
    private final AutowireCapableBeanFactory beanFactory;

    public CancellationController(RegisteredPartnerRepository registeredPartnerRepository, AutowireCapableBeanFactory beanFactory) {
        this.registeredPartnerRepository = registeredPartnerRepository;
        this.beanFactory = beanFactory;
    }

    //@GetMapping("/kuendigung")
    public String index(CancellationForm cancellationForm) {
        return "kuendigung";
    }

    //@PostMapping("/kuendigung")
    public String submitCancellation(@Valid CancellationForm cancellationForm, BindingResult bindingResult) {
        RegisteredPartner partner = registeredPartnerRepository.findByEmail(cancellationForm.getEmail().toLowerCase());
        if (partner == null) {
            bindingResult.addError(new ObjectError("cancellation", "Wir konnte Ihre Daten keinem Vertrag zuordnen."));
            return "kuendigung";
        }

        if (partner.getApproved() != null) {
            bindingResult.addError(new ObjectError("cancellation", "Ihr Vertrag ist bereits zur Kündigung vorgemerkt."));
            return "kuendigung";
        }

        partner.setToken(UUID.randomUUID().toString());
        partner = registeredPartnerRepository.save(partner);
        try {
            sendConfirmationMail(partner);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }

        return "confirmation";

    }

    //@GetMapping("/kuendigung/confirm/{token}")
    public String approve(@PathVariable String token, ApproveModel model) {
        RegisteredPartner partner = registeredPartnerRepository.findByToken(token);
        if (partner == null) {
            model.setError("Der Link ist üngültig.");
            return "approved";
        }

        partner.setApproved(LocalDateTime.now());
        registeredPartnerRepository.save(partner);
        return "approved";
    }

    @PostMapping("/import/partners")
    public ResponseEntity<List<RegisteredPartner>> importPartners(HttpServletRequest request) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line;
        List<RegisteredPartner> importedPartners = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            var tokens = line.split(";");
            if (tokens.length != 2) {
                continue;
            }

            var partner = registeredPartnerRepository.findByEmail(tokens[0]);
            if (partner == null) {
                partner = new RegisteredPartner();
                partner.setId(UUID.randomUUID().toString());
                partner.setEmail(tokens[0]);
                partner.setPartnerNr(tokens[1]);
                partner = registeredPartnerRepository.save(partner);
                importedPartners.add(partner);
            }
        }

        return ResponseEntity.ok(importedPartners);
    }

    @GetMapping("/export/cancellations")
    public void getCancellations(@RequestParam(value = "since", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since,
                                 HttpServletResponse response) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        List<RegisteredPartner> export = registeredPartnerRepository.findByApprovedIsNotNullAndExportedIsNull();

        final var writer = new EscapingCsvWriter(response.getWriter(), ';', '"', '"', "\r\n");
        response.getWriter().print("\uFEFF"); // print BOM

        HeaderColumnNameWithPositionMappingStrategy<RegisteredPartner> mappingStrategy = new HeaderColumnNameWithPositionMappingStrategy<>(this.beanFactory);
        mappingStrategy.setType(RegisteredPartner.class);
        //create a csv writer
        final var csvWriter = new StatefulBeanToCsvBuilder<RegisteredPartner>(writer)
                .withMappingStrategy(mappingStrategy)
                .withEscapechar('"')
                .withQuotechar('"')
                .withSeparator(';')
                .withOrderedResults(false)
                .build();
        csvWriter.write(export.iterator());

        for (var partner : export) {
            partner.setExported(LocalDateTime.now());
            registeredPartnerRepository.save(partner);
        }
    }


    private void sendConfirmationMail(RegisteredPartner registeredPartner) throws MessagingException, IOException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.host", emailSmtpHost);
        properties.put("mail.smtp.port", emailSmtpPort);
        properties.put("mail.smtp.ss.trust", emailSmtpHost);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailSmtpUser, emailSmtpPassword);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailFrom));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(registeredPartner.getEmail()));
        message.setSubject("Bitte bestätigen Sie Ihre Kündigung", "utf-8");

        String body = null;
        try (InputStream inputStream = CancellationController.class.getResourceAsStream("/mail/confirmation.template.html")) {
            if (inputStream == null) {
                throw new IOException("template not found");
            }
            body = IOUtils.toString(inputStream, "utf-8");
        }
        body = body.replace("{{confirmationLink}}", String.format("%s/kuendigung/confirm/%s",
                host, registeredPartner.getToken()));

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(body, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        message.setContent(multipart);
        Transport.send(message);
    }
}
