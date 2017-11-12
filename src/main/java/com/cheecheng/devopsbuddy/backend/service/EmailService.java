package com.cheecheng.devopsbuddy.backend.service;

import com.cheecheng.devopsbuddy.web.domain.frontend.FeedbackPojo;
import org.springframework.mail.SimpleMailMessage;

/**
 * Contract for email service.
 */
public interface EmailService {

    /**
     * Sends an email with the content in the Feedback POJO.
     *
     * @param feedbackPojo  The feedback POJO
     */
    public void sendFeedbackEmail(FeedbackPojo feedbackPojo);

    /**
     * Sends an email with the content of the Simple Mail Message ojbect.
     *
     * @param message   The object containing the email content
     */
    public void sendGenericEmailMessage(SimpleMailMessage message);
}
