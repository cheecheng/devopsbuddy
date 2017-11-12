package com.cheecheng.devopsbuddy.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;

public class MockEmailService extends AbstractEmailService {

    /** The application logger */
    private static final Logger log = LoggerFactory.getLogger(MockEmailService.class);

    /**
     * Sends an email with the content of the Simple Mail Message ojbect.
     *
     * @param message The object containing the email content
     */
    @Override
    public void sendGenericEmailMessage(SimpleMailMessage message) {
        log.debug("Simulating an email service...");
        log.info(message.toString());
        log.debug("Email sent.");
    }
}
