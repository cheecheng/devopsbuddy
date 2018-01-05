package com.cheecheng.devopsbuddy.config;

import com.cheecheng.devopsbuddy.backend.service.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private Environment env;

    private UserSecurityService userSecurityService;

    private static final String[] PUBLIC_MATCHES = {
            "/webjars/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/",
            "/about/**",
            "/contact/**",
            "/error/**/*",
            "/h2-console/**",
            // By default the console will be available at /h2-console.
            // You can customize the console’s path using the spring.h2.console.path property.
    };

    /**
     * Creates an instance with the default configuration enabled.
     */
    @Autowired
    public SecurityConfig(Environment env, UserSecurityService userSecurityService) {
        this.env = env;
        this.userSecurityService = userSecurityService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        List<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains("dev")) {
            // Need to disable CSRF support to access H2 web console or will get error:
            // Invalid CSRF Token 'null' was found on the request parameter '_csrf' or header 'X-CSRF-TOKEN'.
            http.csrf().disable();
            // Need to disable X-Frame-Options, i.e. not to include X-Frame-Options header
            // or will get blank page.
            http.headers().frameOptions().disable();
            // either the above or below works
            //http.headers().frameOptions().sameOrigin();
        }

        http.authorizeRequests()
                .antMatchers(PUBLIC_MATCHES).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/payload")
                .failureUrl("/login?error").permitAll()
                .and()
                .logout().permitAll();
    }


    /**
     * See https://spring.io/blog/2013/07/03/spring-security-java-config-preview-web-security
     * Autowired annotation is to inject AuthenticationManagerBuilder.
     *
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
        auth.userDetailsService(userSecurityService);
    }
}
