package com.lumem.hgest.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

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
            r.requestMatchers("/css/msgform.css","/css/form.css","/css/onehundred.css","/css/background.css",
                            "/register","/register/processing").permitAll()
                    .requestMatchers("/isadmin").hasRole("ADMIN")
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
