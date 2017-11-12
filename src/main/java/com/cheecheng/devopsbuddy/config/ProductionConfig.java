package com.cheecheng.devopsbuddy.config;

import com.cheecheng.devopsbuddy.backend.service.EmailService;
import com.cheecheng.devopsbuddy.backend.service.SmtpEmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailSender;

@Configuration
@Profile("prod")
@PropertySource("file:///${user.home}/.devopsbuddy/application-prod.properties")
public class ProductionConfig {

    // See https://stackoverflow.com/questions/36168675/spring-injecting-constructor-arguments-in-java-configuration-class
    @Bean
    public EmailService emailService(MailSender mailSender) {
        return new SmtpEmailService(mailSender);
    }
}
