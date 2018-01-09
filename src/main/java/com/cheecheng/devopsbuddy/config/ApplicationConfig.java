package com.cheecheng.devopsbuddy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:///${user.home}/.devopsbuddy/application-common.properties")
public class ApplicationConfig {
}
