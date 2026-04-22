package com.apex.timekeeping.config;

import com.apex.timekeeping.security.JwtAuthenticationFilter;
import com.apex.timekeeping.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/notifications/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/health").permitAll()

                // Config write operations – ADMIN only
                .requestMatchers(HttpMethod.POST, "/api/config/departments/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,  "/api/config/departments/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/config/departments/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/config/positions/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,  "/api/config/positions/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/config/employees/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,  "/api/config/employees/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/config/projects/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,  "/api/config/projects/**").hasRole("ADMIN")

                // Notification write – ADMIN or STAFF
                .requestMatchers(HttpMethod.POST, "/api/notifications/**").hasAnyRole("ADMIN","STAFF")
                .requestMatchers(HttpMethod.PUT,  "/api/notifications/**").hasAnyRole("ADMIN","STAFF")
                .requestMatchers(HttpMethod.DELETE,"/api/notifications/**").hasAnyRole("ADMIN","STAFF")

                // Report access
                .requestMatchers("/api/reports/**").hasAnyRole("ADMIN","MANAGER","STAFF")

                // Approval queues
                .requestMatchers("/api/time-explanations/pending").hasAnyRole("ADMIN","MANAGER","STAFF")
                .requestMatchers("/api/leave/requests/pending").hasAnyRole("ADMIN","MANAGER","STAFF")
                .requestMatchers("/api/ot/pending").hasAnyRole("ADMIN","MANAGER")
                .requestMatchers("/api/worklogs/pending").hasAnyRole("ADMIN","MANAGER")

                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration cfg = new org.springframework.web.cors.CorsConfiguration();
        cfg.setAllowedOriginPatterns(java.util.List.of("*"));
        cfg.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        cfg.setAllowedHeaders(java.util.List.of("*"));
        cfg.setAllowCredentials(true);
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
