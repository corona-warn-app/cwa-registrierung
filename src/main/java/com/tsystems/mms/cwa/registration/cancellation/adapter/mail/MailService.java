package com.tsystems.mms.cwa.registration.cancellation.adapter.mail;

import com.tsystems.mms.cwa.registration.cancellation.CancellationController;
import com.tsystems.mms.cwa.registration.model.RegisteredPartner;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class MailService {
    @Value("${email.from}")
    private String emailFrom;

    @Value("${email.smtp-host}")
    private String emailSmtpHost;

    @Value("${email.smtp-port}")
    private String emailSmtpPort;

    @Value("${email.starttls}")
    private String emailStartTls;

    @Value("${email.smtp-user}")
    private String emailSmtpUser;

    @Value("${email.smtp-password}")
    private String emailSmtpPassword;

    public void sendMail(String receiver, String bcc, String subject, String body, Map<String, File> attachments) throws MessagingException, IOException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.host", emailSmtpHost);
        properties.put("mail.smtp.port", emailSmtpPort);
        properties.put("mail.smtp.ssl.trust", emailSmtpHost);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailSmtpUser, emailSmtpPassword);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailFrom));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
        if (bcc != null && bcc.trim().length() > 0) {
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
        }
        message.setSubject(subject, "utf-8");

        Multipart multipart = new MimeMultipart();

        var bodyPart = new MimeBodyPart();
        bodyPart.setContent(body, "text/html; charset=utf-8");
        multipart.addBodyPart(bodyPart);

        if (attachments != null) {
            for (Map.Entry<String, File> attachment : attachments.entrySet()) {
                if (attachment != null && attachment.getValue().exists()) {
                    var attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(attachment.getValue());
                    attachmentPart.setFileName(attachment.getKey());
                    multipart.addBodyPart(attachmentPart);
                }
            }
        }

        message.setContent(multipart);
        Transport.send(message);
    }
}
