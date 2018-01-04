package com.cheecheng.devopsbuddy.bootstrap;

import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.Role;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.User;
import com.cheecheng.devopsbuddy.backend.service.UserService;
import com.cheecheng.devopsbuddy.enums.PlansEnum;
import com.cheecheng.devopsbuddy.enums.RolesEnum;
import com.cheecheng.devopsbuddy.utils.UsersUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.HashSet;
import java.util.Set;

public class H2Bootstrap implements CommandLineRunner {

    /** The application logger */
    private static final Logger log = LoggerFactory.getLogger(H2Bootstrap.class);

    private UserService userService;

    @Autowired
    public H2Bootstrap(UserService userService) {
        this.userService = userService;
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        User user = UsersUtils.createBasicUser();
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(RolesEnum.BASIC));
        log.debug("Creating user with username {}", user.getUsername());
        userService.createUser(user, PlansEnum.PRO, roles);
        log.debug("User {} created", user.getUsername());
    }
}
