package com.cheecheng.devopsbuddy.test.integration;

import com.cheecheng.devopsbuddy.DevopsbuddyApplication;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.User;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.PasswordResetTokenRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class PasswordResetTokenIntegrationTest extends AbstractIntegrationTest {

    @Value("${token.expiration.length.minutes}")
    private int expirationTimeInMinutes;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void init() throws Exception {
        Assert.assertFalse(expirationTimeInMinutes == 0);
    }

    @Test
    public void testTokenExpirationLength() {

        User user = createUser(testName);
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());

        // https://docs.oracle.com/javase/9/docs/api/java/time/Clock.html#systemUTC--
        // This clock, rather than systemDefaultZone(), should be used when you need the current instant without the date or time.
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        String token = UUID.randomUUID().toString();

        LocalDateTime expectedExpirationTime = now.plusMinutes(expirationTimeInMinutes);

        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);

        LocalDateTime actualExpirationTime = passwordResetToken.getExpiryDate();
        Assert.assertNotNull(actualExpirationTime);
        Assert.assertEquals(expectedExpirationTime, actualExpirationTime);
    }

    @Test
    public void testFindTokenbyTokenValue() {

        User user = createUser(testName);
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        createPasswordResetToken(token, user, now);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        Assert.assertNotNull(passwordResetToken);
        Assert.assertNotNull(passwordResetToken.getId());
        Assert.assertNotNull(passwordResetToken.getUser());
    }

    @Test
    public void testDeleteToken() {

        User user = createUser(testName);

        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);
        long tokenId = passwordResetToken.getId();
        passwordResetTokenRepository.delete(tokenId);

        PasswordResetToken shouldNotExistToken = passwordResetTokenRepository.findOne(tokenId);
        Assert.assertNull(shouldNotExistToken);
    }

    /**
     * test to make sure when a user is deleted, all tokens are deleted.
     * This works because in User.java, cascade = CascadeType.ALL for Set<PasswordResetToken>
     */
    @Test
    public void testCascadeDeleteFromUserEntity() {

        User user = createUser(testName);
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);
        passwordResetToken.getId();

        userRepository.delete(user.getId());

        Set<PasswordResetToken> shouldBeEmpty = passwordResetTokenRepository.findAllByUserId(user.getId());
        Assert.assertTrue(shouldBeEmpty.isEmpty());

    }

    @Test
    public void testMultipleTokensAreReturnedWhenQueringByUserId() {

        User user = createUser(testName);
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        String token1 = UUID.randomUUID().toString();
        String token2 = UUID.randomUUID().toString();
        String token3 = UUID.randomUUID().toString();

        Set<PasswordResetToken> tokens = new HashSet<>();
        tokens.add(createPasswordResetToken(token1, user, now));
        tokens.add(createPasswordResetToken(token2, user, now));
        tokens.add(createPasswordResetToken(token3, user, now));

        // This is not needed, as save() is done in createPasswordResetToken(), but no harm to save again.
        // Call was ignored if no change.
        passwordResetTokenRepository.save(tokens);

        User foundUser = userRepository.findOne(user.getId());

        Set<PasswordResetToken> actualTokens = passwordResetTokenRepository.findAllByUserId(foundUser.getId());
        Assert.assertTrue(actualTokens.size() == tokens.size());

        List<String> tokensAsList = tokens.stream().map(prt -> prt.getToken()).collect(Collectors.toList());
        List<String> actualTokensAsList = actualTokens.stream().map(prt -> prt.getToken()).collect(Collectors.toList());
        Assert.assertEquals(tokensAsList, actualTokensAsList);
    }

    // ------------ Private methods

    private PasswordResetToken createPasswordResetToken(String token, User user, LocalDateTime now) {

        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, now, expirationTimeInMinutes);
        passwordResetTokenRepository.save(passwordResetToken);
        Assert.assertNotNull(passwordResetToken.getId());
        return passwordResetToken;
    }
}
