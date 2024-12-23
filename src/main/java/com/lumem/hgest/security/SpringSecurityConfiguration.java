package com.lumem.hgest.security;

import com.lumem.hgest.repository.UserInfoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SpringSecurityConfiguration{

    @Bean
    public HGestUserDetailsService hGestUserDetailsService(UserInfoRepository Source){
        return new HGestUserDetailsService(Source);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain FilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(r -> r.anyRequest().permitAll());


        http.formLogin(withDefaults());

        http.csrf((csrf) -> csrf
                .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
        );

        http.rememberMe(withDefaults());

        return http.build();
    }

}
