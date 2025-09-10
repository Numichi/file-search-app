package com.example.demo.config;

import com.example.api.AuthenticationV1Api;
import com.example.api.NoteV1Api;
import com.example.demo.config.filter.JwtAuthFilter;
import com.example.demo.config.security.CustomAccessDeniedHandler;
import com.example.demo.config.security.CustomAuthenticationEntryPoint;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.passay.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    private final String[] anonymousPaths = {
        AuthenticationV1Api.PATH_LOGIN,
        AuthenticationV1Api.PATH_REGISTER,
    };

    private final String[] permitAllPaths = {
        "/api/openapi.yaml",
        "/api/swagger",
        "/api/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };

    private final String[] roleUserPaths = {
        NoteV1Api.PATH_CREATE_NOTE,
        NoteV1Api.PATH_GET_NOTES,
        NoteV1Api.PATH_DELETE_NOTE_BY_ID
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(permitAllPaths).permitAll()
                .requestMatchers(anonymousPaths).anonymous()
                .requestMatchers(roleUserPaths).hasRole("USER")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ehc -> ehc
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public Validator defaultValidator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public PasswordValidator passwordValidator() {
        return new PasswordValidator(
            new LengthRule(8, 256),
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            new CharacterRule(EnglishCharacterData.Digit, 1),
            new CharacterRule(EnglishCharacterData.Special, 1),
            new WhitespaceRule()
        );
    }
}
