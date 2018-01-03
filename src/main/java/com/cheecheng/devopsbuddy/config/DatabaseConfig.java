package com.cheecheng.devopsbuddy.config;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.cheecheng.devopsbuddy.backend.persistence.repositories")
public class DatabaseConfig {
}

/*
Can also use @EnableJpaRepositories in the main application where @SpringBootApplication is located.
Like this,

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.cheecheng.devopsbuddy.backend.persistence.repositories")
public class DevopsbuddyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevopsbuddyApplication.class, args);
	}
}

 */