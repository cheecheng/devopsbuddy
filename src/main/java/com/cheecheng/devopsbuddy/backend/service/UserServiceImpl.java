package com.cheecheng.devopsbuddy.backend.service;

import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.Role;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.User;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.cheecheng.devopsbuddy.enums.PlansEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true) // default transaction for the whole class
public class UserServiceImpl implements UserService {

    private RoleRepository roleRepository;
    private PlanRepository planRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    /** The application logger */
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(RoleRepository roleRepository, PlanRepository planRepository, UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional  // specific transaction
    @Override
    public User createUser(User user, PlansEnum plansEnum, Set<UserRole> userRoles) {

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        Plan plan = new Plan(plansEnum);
        // It makes sure the plans exist in the database
        if (!planRepository.exists(plansEnum.getId())) {
            planRepository.save(plan);
        }

        user.setPlan(plan);

        for (UserRole userRole : userRoles) {
            roleRepository.save(userRole.getRole());
        }

        user.getUserRoles().addAll(userRoles);

        userRepository.save(user);

        // at this point, user.id is generated because of @GeneratedValue(strategy = GenerationType.AUTO)

        return user;
    }

    @Override
    public User findById(long userId) {
        return userRepository.findOne(userId);
    }

    @Transactional
    @Override
    public void updateUserPassword(long userId, String password) {
        password = passwordEncoder.encode(password);
        userRepository.updateUserPassword(userId, password);
        log.debug("Password updated successfully for user id {}", userId);
    }
}

/*
https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/annotation/Transactional.html

 */