package com.cheecheng.devopsbuddy.web.controllers;

import com.cheecheng.devopsbuddy.backend.service.EmailService;
import com.cheecheng.devopsbuddy.web.domain.frontend.FeedbackPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContactController {

    /** The application logger */
    private static final Logger log = LoggerFactory.getLogger(ContactController.class);

    /** The key which identifies the feedback payload in the Model */
    public static final String FEEDBACK_MODEL_KEY = "feedback";

    /** The Contact Us view name */
    public static final String CONTACT_US_VIEW_NAME = "contact/contact";  // folder/view name

    private EmailService emailService;

    @Autowired
    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    // or @RequestMapping(value = "/contact", method = RequestMethod.GET)
    @GetMapping(value = "/contact") // or @GetMapping("/contact")
    public String contactGet(ModelMap model) {
        FeedbackPojo feedbackPojo = new FeedbackPojo();
        model.addAttribute(FEEDBACK_MODEL_KEY, feedbackPojo);
        return CONTACT_US_VIEW_NAME;
    }

    // or @RequestMapping(value = "/contact", method = RequestMethod.POST)
    @PostMapping("/contact")    // or @PostMapping(value = "/contact")
    public String contactPost(@ModelAttribute(FEEDBACK_MODEL_KEY) FeedbackPojo feedback) {
        log.debug("Feedback POJO content: {}", feedback);
        emailService.sendFeedbackEmail(feedback);
        return CONTACT_US_VIEW_NAME;
    }
}
