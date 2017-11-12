package com.cheecheng.devopsbuddy.backend.service;

import com.cheecheng.devopsbuddy.web.domain.frontend.FeedbackPojo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

public abstract class AbstractEmailService implements EmailService {

    @Value("${default.to.address}")
    private String defaultToAddress;

    @Value("${spring.mail.username}")
    private String fromAddress;

    /**
     * Create a Simple Mail Message from a feedback POJO.
     *
     * @param feedback  The Feedback POJO
     * @return
     */
    protected SimpleMailMessage prepareSimpleMailMessageFromFeedbackPojo(FeedbackPojo feedback) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(defaultToAddress);
        message.setFrom(fromAddress);
        message.setReplyTo(feedback.getEmail());
        message.setSubject("[DevOps Buddy]: Feedback received from " + feedback.getFirstName() + " " +
                feedback.getLastName() + "!");
        message.setText(feedback.getFeedback());
        return message;
    }

    @Override
    public void sendFeedbackEmail(FeedbackPojo feedbackPojo) {
        sendGenericEmailMessage(prepareSimpleMailMessageFromFeedbackPojo(feedbackPojo));
    }
}
