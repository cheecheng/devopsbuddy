package com.cheecheng.devopsbuddy.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class SmtpEmailService extends AbstractEmailService {

    /** The application logger */
    private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);

    private MailSender mailSender;

    @Autowired
    public SmtpEmailService(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email with the content of the Simple Mail Message ojbect.
     *
     * @param message The object containing the email content
     */
    @Override
    public void sendGenericEmailMessage(SimpleMailMessage message) {
        log.debug("Sending email for : {}", message);
        mailSender.send(message);
        log.info("Email sent.");
    }
}
