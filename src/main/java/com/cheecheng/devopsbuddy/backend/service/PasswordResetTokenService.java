package com.cheecheng.devopsbuddy.backend.service;

import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.User;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.PasswordResetTokenRepository;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.util.Password;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PasswordResetTokenService {

    private UserRepository userRepository;
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${token.expiration.length.minutes}")
    private int tokenExpirationInMinutes;

    /** The application logger */
    private static final Logger log = LoggerFactory.getLogger(PasswordResetTokenService.class);

    @Autowired
    public PasswordResetTokenService(UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    /**
     * Retrieves a PasswordResetToken for the given token.
     *
     * @param token The token to be returned
     * @return  A PasswordResetToken if one was found or null if none was found.
     */
    public PasswordResetToken findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    /**
     * Creates a new PasswordResetToken for the user identified by the given email.
     *
     * @param email The email uniquely identifying the user
     * @return  a new PasswordResetToken for the user identified by the given email or null if none was found.
     */
    @Transactional
    public PasswordResetToken createPasswordResetTokenForEmail(String email) {

        PasswordResetToken passwordResetToken = null;

        User user = userRepository.findByEmail(email);

        if (null != user) {
            String token = UUID.randomUUID().toString();
            LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
            passwordResetToken = new PasswordResetToken(token, user, now, tokenExpirationInMinutes);

            passwordResetToken = passwordResetTokenRepository.save(passwordResetToken);
            log.debug("Successfully created token {} for user {}", token, user.getUsername());
        } else {
            log.warn("We couldn't find a user with the given email {}", email);
        }

        return passwordResetToken;
    }
}
