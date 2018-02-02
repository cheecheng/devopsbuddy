package com.cheecheng.devopsbuddy.test.integration;

import com.cheecheng.devopsbuddy.DevopsbuddyApplication;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.Role;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.User;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.cheecheng.devopsbuddy.backend.service.UserService;
import com.cheecheng.devopsbuddy.enums.PlansEnum;
import com.cheecheng.devopsbuddy.enums.RolesEnum;
import com.cheecheng.devopsbuddy.utils.UserUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class UserServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testCreateNewUser() {
        User user = createUser(testName);

        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());
    }

    @Test
    public void testUpdateUserPassword() {
        User user = createUser(testName);
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());

        String newPassword = UUID.randomUUID().toString();
        userService.updateUserPassword(user.getId(), newPassword);
        User updatedUser = userService.findById(user.getId());
        String encryptedPassword = passwordEncoder.encode(newPassword);

        //Assert.assertEquals(encryptedPassword, updatedUser.getPassword());
        // Will figure out later
    }
}
