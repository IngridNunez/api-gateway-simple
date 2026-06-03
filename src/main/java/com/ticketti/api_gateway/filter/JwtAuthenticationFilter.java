package com.ticketti.api_gateway.filter;

import java.util.Set;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.ticketti.api_gateway.config.JwtService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Filtro global para autenticación JWT en el API Gateway.
 * Las rutas públicas pasan sin token; las demás requieren Authorization Bearer.
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String HEADER_X_FORWARDED_PROTO = "X-Forwarded-Proto";
    private static final String HEADER_X_REQUEST_ID = "X-Request-ID";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String requestId = request.getHeaders().getFirst(HEADER_X_REQUEST_ID);

        if (esRutaPublica(path)) {
            log.debug("Ruta pública en API Gateway, no se valida JWT: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Falta o es inválido el header Authorization para la ruta: {}, requestId={}", path, requestId);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtService.validateToken(token)) {
            log.warn("Token JWT inválido o expirado para la ruta: {}, requestId={}", path, requestId);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String username = jwtService.extractUsername(token);
        Set<String> roles = jwtService.extractRoles(token);

        log.debug("Usuario autenticado: {}, roles: {}", username, roles);

        ServerHttpRequest.Builder builder = request.mutate()
                .header("X-Usuario", username)
                .header(
                        "X-Forwarded-For",
                        request.getRemoteAddress() != null
                                ? request.getRemoteAddress().getAddress().getHostAddress()
                                : "unknown"
                )
                .header(HEADER_X_FORWARDED_PROTO, obtenerForwardedProto(request))
                .header(HEADER_X_REQUEST_ID, requestId != null && !requestId.isBlank() ? requestId : "unknown");

        if (roles != null && !roles.isEmpty()) {
            builder.header("X-Usuario-Rol", String.join(",", roles));
        }

        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    private boolean esRutaPublica(String path) {
        return path.startsWith("/auth/login")
                || path.equals("/api/v1/usuarios")
                || path.startsWith("/api/v1/usuarios/validar-credenciales")
                || path.startsWith("/actuator/");
    }

    private String obtenerForwardedProto(ServerHttpRequest request) {
        String forwardedProto = request.getHeaders().getFirst(HEADER_X_FORWARDED_PROTO);

        if (forwardedProto != null && !forwardedProto.isBlank()) {
            return forwardedProto;
        }

        String scheme = request.getURI().getScheme();
        return scheme != null && !scheme.isBlank() ? scheme : "http";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}