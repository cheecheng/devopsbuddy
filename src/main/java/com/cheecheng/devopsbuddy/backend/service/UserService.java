package com.cheecheng.devopsbuddy.backend.service;

import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.Role;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.User;
import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.cheecheng.devopsbuddy.enums.PlansEnum;

import java.util.Set;

public interface UserService {

    User createUser(User user, PlansEnum plansEnum, Set<UserRole> userRoles);
}
