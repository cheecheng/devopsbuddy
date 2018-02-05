package com.cheecheng.devopsbuddy.web.controllers;

import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.User;
import com.cheecheng.devopsbuddy.backend.service.EmailService;
import com.cheecheng.devopsbuddy.backend.service.PasswordResetTokenService;
import com.cheecheng.devopsbuddy.utils.UserUtils;
import com.cheecheng.devopsbuddy.backend.service.I18NService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ForgotMyPasswordController {

    /** The application logger */
    private static final Logger log = LoggerFactory.getLogger(ForgotMyPasswordController.class);

    public static final String EMAIL_ADDRESS_VIEW_NAME = "forgotmypassword/emailForm";

    public static final String FORGOT_PASSWORD_URL_MAPPING = "/forgotmypassword";

    public static final String MAIL_SENT_KEY = "mailSent";
    public static final String CHANGE_PASSWORD_PATH = "/changeuserpassword";
    public static final String EMAIL_MESSAGE_TEXT_PROPERTY_NAME = "forgotmypassword.email.text";

    private PasswordResetTokenService passwordResetTokenService;
    private EmailService emailService;
    private I18NService i18NService;

    @Value("${webmaster.email}")
    private String webMasterEmail;

    @Autowired
    public ForgotMyPasswordController(PasswordResetTokenService passwordResetTokenService, EmailService emailService, I18NService i18NService) {
        this.passwordResetTokenService = passwordResetTokenService;
        this.emailService = emailService;
        this.i18NService = i18NService;
    }

    // Can use either @GetMapping() or @RequestMapping()
    @GetMapping(FORGOT_PASSWORD_URL_MAPPING)
    //@RequestMapping(value = FORGOT_PASSWORD_URL_MAPPING, method = RequestMethod.GET)
    public String forgotPasswordGet() {
        return EMAIL_ADDRESS_VIEW_NAME;
    }

    // See http://www.baeldung.com/spring-mvc-model-model-map-model-view
    // and https://stackoverflow.com/questions/18486660/what-are-the-differences-between-model-modelmap-and-modelandview
    // Also see my spring.docx file.

    // Can use either @GetMapping() or @RequestMapping()
    @PostMapping(FORGOT_PASSWORD_URL_MAPPING)
    //@RequestMapping(value = FORGOT_PASSWORD_URL_MAPPING, method = RequestMethod.POST)
    public String forgotPasswordPost(HttpServletRequest request,
                                     String email,
                                     //ModelMap model) {
                                     Model model) {
                                     // Can use either ModelMap or Model here
        PasswordResetToken passwordResetToken = passwordResetTokenService.createPasswordResetTokenForEmail(email);

        if (null == passwordResetToken) {
            // don't do anything if email doesn't exist. just log it.
            // don't want to let hacker know what email exist.
            log.warn("Couldn't find a password reset token for email {}", email);
        } else {
            User user = passwordResetToken.getUser();
            String token = passwordResetToken.getToken();

            String resetPasswordUrl = UserUtils.createPasswordResetUrl(request, user.getId(), token);
            log.debug("Reset Password URL {}", resetPasswordUrl);

            String emailText = i18NService.getMessage(EMAIL_MESSAGE_TEXT_PROPERTY_NAME, request.getLocale());

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("[Devopsbuddy]: How to Reset Your Password");
            mailMessage.setText(emailText + "\r\n" + resetPasswordUrl);
            mailMessage.setFrom(webMasterEmail);

            emailService.sendGenericEmailMessage(mailMessage);

            //log.info("Token value: {} for email: {}", passwordResetToken.getToken(), email);
            //log.debug("Username {}", passwordResetToken.getUser().getUsername());
        }

        model.addAttribute(MAIL_SENT_KEY, "true");

        return EMAIL_ADDRESS_VIEW_NAME;
    }
}
