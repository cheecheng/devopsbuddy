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
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testCreateNewUser() {

        // Use test name as test username and email
        String username = testName.getMethodName() + "1";
        String email = testName.getMethodName() + "1@devopsbuddy.com";

        Set<UserRole> userRoles = new HashSet<>();
        User basicUser = UserUtils.createBasicUser(username, email);
        userRoles.add(new UserRole(basicUser, new Role(RolesEnum.BASIC)));

        User user = userService.createUser(basicUser, PlansEnum.BASIC, userRoles);
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());
    }
}
