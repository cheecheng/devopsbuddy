package com.cheecheng.devopsbuddy.test.unit;

import com.cheecheng.devopsbuddy.utils.UserUtils;
import com.cheecheng.devopsbuddy.web.controllers.ForgotMyPasswordController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.UUID;

public class UserUtilsUnitTest {

    private MockHttpServletRequest mockHttpServletRequest;

    @Before
    public void setUp() throws Exception {
        mockHttpServletRequest = new MockHttpServletRequest();
    }

    @Test
    public void testPasswordResetEmailUrlConstruction() {
        mockHttpServletRequest.setServerPort(8080); // Default is 80

        String token = UUID.randomUUID().toString();
        long userId = 123456;

        String expectedUrl = "http://localhost:8080" +
                ForgotMyPasswordController.CHANGE_PASSWORD_PATH + "?id=" + userId + "&token=" + token;

        String actualUrl = UserUtils.createPasswordResetUrl(mockHttpServletRequest, userId, token);

        Assert.assertEquals(expectedUrl, actualUrl);
    }
}
