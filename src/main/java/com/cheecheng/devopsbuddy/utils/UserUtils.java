package com.cheecheng.devopsbuddy.utils;

import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.User;

public class UserUtils {

    /**
     * Non instantiable
     */
    private UserUtils() {
        throw new AssertionError("Non instantiable");
    }

    /**
     * Create a user with basic attribute set.
     *
     * @param username
     * @param email
     * @return a User entity
     */
    public static User createBasicUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("secret");
        user.setEmail(email);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPhoneNumber("123456789123");
        user.setCountry("GB");
        user.setEnabled(true);
        user.setDescription("A basic user");
        user.setProfileImageUrl("https://blabla.images.com/basicuser");

        return user;
    }
}
