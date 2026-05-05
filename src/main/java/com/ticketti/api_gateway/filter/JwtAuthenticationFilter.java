package com.ticketti.api_gateway.filter;

import com.ticketti.api_gateway.config.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    // Rutas públicas que no requieren autenticación
    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/",
            "/auth/login",
            "/auth/register",
            "/actuator/",
            "/fallback/",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/webjars",
            "/eureka"
    );

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.debug("Processing request: {}", path);

        // Permitir preflight CORS sin autenticación
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            return handlePreflight(exchange);
        }

        // Registro público de usuarios (sin token)
        if (HttpMethod.POST.equals(request.getMethod()) && "/api/v1/usuarios".equals(path)) {
            return chain.filter(exchange);
        }

        // Verificar si la ruta es pública
        if (isPublicPath(path)) {
            log.debug("Public path accessed: {}", path);
            return chain.filter(exchange);
        }

        // Obtener el header de autorización
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        // Validar el token
        if (!Boolean.TRUE.equals(jwtService.validateToken(token))) {
            log.warn("Invalid or expired token for path: {}", path);
            return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // Extraer información del usuario y agregarla a los headers
        String username = jwtService.extractUsername(token);
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Name", username)
                .header("X-User-Roles", String.join(",", jwtService.extractRoles(token)))
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        log.debug("Token validated successfully for user: {} on path: {}", username, path);
        return chain.filter(mutatedExchange);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream()
                .anyMatch(path::startsWith);
    }

    private Mono<Void> handlePreflight(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        String origin = exchange.getRequest().getHeaders().getFirst(HttpHeaders.ORIGIN);

        response.setStatusCode(HttpStatus.OK);
        addCorsHeaders(response, origin);
        return response.setComplete();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        String origin = exchange.getRequest().getHeaders().getFirst(HttpHeaders.ORIGIN);

        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json");
        addCorsHeaders(response, origin);

        String errorBody = String.format("{\"error\": \"%s\", \"status\": %d}", err, httpStatus.value());

        return response.writeWith(Mono.just(response.bufferFactory()
                .wrap(errorBody.getBytes(StandardCharsets.UTF_8))));
    }

    private void addCorsHeaders(ServerHttpResponse response, String origin) {
        if (origin != null && !origin.isBlank()) {
            response.getHeaders().set("Access-Control-Allow-Origin", origin);
        }
        response.getHeaders().set("Vary", "Origin");
        response.getHeaders().set("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH");
        response.getHeaders().set("Access-Control-Allow-Headers", "Authorization,Content-Type,Accept,Origin");
        response.getHeaders().set("Access-Control-Allow-Credentials", "true");
    }

    @Override
    public int getOrder() {
        return -100; // Ejecutar antes que otros filtros
    }
}
