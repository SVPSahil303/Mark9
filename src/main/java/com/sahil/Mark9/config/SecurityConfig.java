package com.sahil.Mark9.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.sahil.Mark9.service.CustomParentDetailsService;

@Configuration
public class SecurityConfig {
    
    private final CustomParentDetailsService parentDetailsService;

    public SecurityConfig(CustomParentDetailsService parentDetailsService) {
        this.parentDetailsService = parentDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
            .csrf(csrf->csrf.disable())
           .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/register",
                    "/login",
                    "/",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/videos/**",

                    // 👇 CHILD ROUTES
                    "/child/login",
                    "/child/logout",
                    "/child/learn",
                    "/child/progress/**",
                    "/api/child/**"
                ).permitAll()
                .requestMatchers("/parent/**").hasRole("PARENT").anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/parent/dashboard",true)
                .permitAll()
            ).logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            ).sessionManagement(sm -> sm
                .sessionFixation(sf -> sf.migrateSession())
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // pass your CustomParentDetailsService (it implements UserDetailsService)
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(parentDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

}
