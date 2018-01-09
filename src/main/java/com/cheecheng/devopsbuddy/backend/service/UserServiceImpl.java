package com.cheecheng.devopsbuddy.backend.service;

import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.Role;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.User;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.cheecheng.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.cheecheng.devopsbuddy.enums.PlansEnum;
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
}

/*
https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/annotation/Transactional.html

 */