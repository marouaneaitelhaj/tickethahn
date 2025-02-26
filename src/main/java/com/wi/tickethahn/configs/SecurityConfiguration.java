package com.wi.tickethahn.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private final JwtAuthFilter jwtAuthFilter;


    private static final String[] WHITE_LIST_URL = {
            "/api/auth",
            "/api/auth/signup",
            "/api/users/batch",
            "/api/users/generate",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/h2-console/**",
            "/api/usersfactorie/**",
            "/swagger-ui/**",
            "/v2/api-docs",
            "/api/v1/user/all"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("SecurityConfiguration.securityFilterChain");
        System.out.println(WHITE_LIST_URL);
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers().frameOptions().disable();
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(WHITE_LIST_URL)
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterAfter(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
