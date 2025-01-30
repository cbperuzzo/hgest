package com.lumem.hgest.security;

import com.lumem.hgest.model.StoredUser;
import com.lumem.hgest.repository.StoredUserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.ProviderManagerBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices.RememberMeTokenAlgorithm;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;

import javax.xml.transform.Source;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
public class SpringSecurityConfiguration{
    private HGestUserDetailsService userDetailsService;

    public SpringSecurityConfiguration(HGestUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain FilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(r -> {
            r.requestMatchers("/css/login.css","/register","/css/register.css","/register/processing").permitAll()
            .anyRequest().authenticated();
        });

        http.formLogin( x ->
        {
            x.loginPage("/login").permitAll();
            x.successForwardUrl("/home");
            x.loginProcessingUrl("/login/processing").permitAll();
            x.usernameParameter("username");
            x.passwordParameter("password");

        });

        http.logout(l -> {
            l.logoutUrl("/logout").permitAll();
            l.logoutSuccessUrl("/login");
            l.addLogoutHandler(new HeaderWriterLogoutHandler(
                    new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.COOKIES))
            );

        });

        http.rememberMe(withDefaults());

        http.csrf((csrf) -> csrf
                .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
        );

        http.authenticationProvider(new HGestAuthenticationProvider(userDetailsService,passwordEncoder()));
        http.userDetailsService(userDetailsService);

        return http.build();
    }

}
