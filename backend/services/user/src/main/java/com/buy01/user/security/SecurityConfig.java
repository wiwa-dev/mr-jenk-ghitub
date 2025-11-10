    package com.buy01.user.security;

    import org.springframework.http.HttpStatus;

    import java.util.HashMap;
    import java.util.Map;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    import com.fasterxml.jackson.databind.ObjectMapper;

    import jakarta.servlet.http.HttpServletResponse;

    @Configuration
    @EnableMethodSecurity
    @EnableWebSecurity
    public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
        }

        public static void writeJsonError(HttpServletResponse response, HttpStatus status,
                String error, String message, String path) throws java.io.IOException {
            response.setStatus(status.value());
            response.setContentType("application/json");

            Map<String, Object> body = new HashMap<>();
            body.put("status", status.value());
            body.put("error", error);
            body.put("message", message);
            body.put("path", path);

            new ObjectMapper().writeValue(response.getOutputStream(), body);
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
                throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .httpBasic(httpBasic -> httpBasic.disable()) // désactive Basic Auth
                    .formLogin(form -> form.disable()) // désactive /login par défaut
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .exceptionHandling(exception -> exception
                            // 401 → Non authentifié
                            .authenticationEntryPoint((request, response, authException) -> {
                                writeJsonError(response, HttpStatus.UNAUTHORIZED, "No authenticated",
                                        "You would be connected for acces at this ressources", request.getRequestURI());
                            })

                            // 403 → Authentifié mais pas autorisé
                            .accessDeniedHandler((request, response, accessDeniedException) -> {
                                writeJsonError(response, HttpStatus.FORBIDDEN, "Acces Denied",
                                        "You do not have the necessary permissions", request.getRequestURI());
                            })
                            
                            )
                    // .authorizeHttpRequests(auth -> auth
                    //         // // 🔓 Autoriser les routes publiques
                    //         // .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                    //         // .requestMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll()
                    //         // .requestMatchers(HttpMethod.GET, "/api/v1/users/health").permitAll()
                    //         // // 🔒 Tout le reste est protégé
                    //         .anyRequest().permitAll())
                    // Ajout du filtre JWT
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }
    }
