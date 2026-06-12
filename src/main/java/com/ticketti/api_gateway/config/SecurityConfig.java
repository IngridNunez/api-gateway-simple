package com.ticketti.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración de seguridad de Spring Security para la aplicación.
 * Deja la API accesible sin autenticación y deshabilita mecanismos interactivos.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
         * Define la cadena de filtros de seguridad de Spring Security.
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
                .anyExchange().permitAll()
                )
                .build();
    }
}