package com.ticketti.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración de seguridad de Spring Security para el API Gateway.
 * Define rutas públicas y protegidas, y deshabilita autenticación básica y formularios.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Define la cadena de filtros de seguridad de Spring Security para el gateway.
     * Configura el acceso a rutas públicas y protege el resto con autenticación.
     *
     * @param http objeto de configuración de seguridad HTTP reactiva
     * @return cadena de filtros de seguridad configurada
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                        .pathMatchers("/auth/**").permitAll()
                        .anyExchange().authenticated()
                )
                .build();
    }
}
