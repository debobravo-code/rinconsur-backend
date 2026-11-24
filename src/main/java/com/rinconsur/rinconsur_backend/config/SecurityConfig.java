
package com.rinconsur.rinconsur_backend.config;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                .dispatcherTypeMatchers(
                    DispatcherType.ERROR).permitAll()

                // Autenticacion
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/auth/login"
                ).permitAll()

                // Usuarios y tipos de usuario
                .requestMatchers(
                    "/api/users",
                    "/api/users/**",
                    "/api/tipos-usuario",
                    "/api/tipos-usuario/**"
                ).hasRole("ADMIN")

                // Sedes
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/sedes",
                    "/api/sedes/**"
                ).hasAnyRole("ADMIN", "PERSONAL")

                .requestMatchers(
                    "/api/sedes",
                    "/api/sedes/**"
                ).hasRole("ADMIN")

                // Probadores
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/probadores",
                    "/api/probadores/**"
                ).hasAnyRole("ADMIN", "PERSONAL")

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/probadores/*/ocupar",
                    "/api/probadores/*/liberar",
                    "/api/probadores/*/estado"
                ).hasAnyRole("ADMIN", "PERSONAL")

                .requestMatchers(
                    "/api/probadores",
                    "/api/probadores/**"
                ).hasRole("ADMIN")

                // Historial de ocupaciones
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/registros",
                    "/api/registros/**"
                ).hasAnyRole("ADMIN", "PERSONAL")

                // Reportes de prendas
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/reportes-prenda",
                    "/api/reportes-prenda/**"
                ).hasAnyRole("ADMIN", "PERSONAL")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/reportes-prenda"
                ).hasAnyRole("ADMIN", "PERSONAL")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/reportes-prenda/*"
                ).hasAnyRole("ADMIN", "PERSONAL")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/reportes-prenda/*"
                ).hasRole("ADMIN")

                // Fotografias
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/fotos"
                ).hasAnyRole("ADMIN", "PERSONAL")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/fotos/*"
                ).hasAnyRole("ADMIN", "PERSONAL")

                // Bloquear solicitudes no autorizadas
                .anyRequest().denyAll()
            )

            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
