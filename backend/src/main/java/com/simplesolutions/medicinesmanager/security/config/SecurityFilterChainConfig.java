package com.simplesolutions.medicinesmanager.security.config;

import com.simplesolutions.medicinesmanager.security.jwt.JWTAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Value("#{'${security-chain.allowed-swagger-endpoints}'.split(',')}")
    private String[] SWAGGER_ENDPOINTS;

    @Value("#{'${security-chain.unauthenticated.post-endpoints}'.split(',')}")
    private String[] UNAUTHENTICATED_POST_ENDPOINTS;

    @Value("#{'${security-chain.unauthenticated.get-endpoints}'.split(',')}")
    private String[] UNAUTHENTICATED_GET_ENDPOINTS;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, UNAUTHENTICATED_POST_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, UNAUTHENTICATED_GET_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, SWAGGER_ENDPOINTS).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(management ->
                        management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(configurer ->
                        configurer.authenticationEntryPoint(authenticationEntryPoint));

        return http.build();
    }
}
