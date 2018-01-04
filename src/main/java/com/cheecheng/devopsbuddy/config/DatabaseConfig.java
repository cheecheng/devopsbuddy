package com.cheecheng.devopsbuddy.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.cheecheng.devopsbuddy.backend.persistence.repositories")
@EntityScan(basePackages = "com.cheecheng.devopsbuddy.backend.persistence.domain.backend")
@EnableTransactionManagement
public class DatabaseConfig {
}

/*
All these annotations are optional:
@Configuration
@EnableJpaRepositories(basePackages = "com.cheecheng.devopsbuddy.backend.persistence.repositories")
@EntityScan(basePackages = "com.cheecheng.devopsbuddy.backend.persistence.domain.backend")
@EnableTransactionManagement
as long as the repositories and entities are within the base packages of the application

Can also use @EnableJpaRepositories in the main application where @SpringBootApplication is located.
Like this,

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.cheecheng.devopsbuddy.backend.persistence.repositories")
public class DevopsbuddyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevopsbuddyApplication.class, args);
	}
}

https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/domain/EntityScan.html
http://therealdanvega.com/blog/2017/03/22/spring-boot-entity-scan
 */