package com.ticketti.api_gateway.filter;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
 * Filtro global para autenticación JWT en el API Gateway. Intercepta todas las
 * peticiones y valida el token JWT antes de permitir el acceso.
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtService jwtService;

    /**
     * Filtra las peticiones entrantes validando el token JWT. Las rutas
     * públicas (/auth/**) son excluidas de la validación.
     *
     * @param exchange el intercambio del servidor web
     * @param chain la cadena de filtros
     * @return Mono que completa el procesamiento de la petición
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (esRutaPublica(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Falta o es invalido el header Authorization para la ruta: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtService.validateToken(token)) {
            log.warn("Token JWT invalido o expirado para la ruta: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String username = jwtService.extractUsername(token);
        Set<String> roles = jwtService.extractRoles(token);

        log.debug("Usuario autenticado: {}, roles: {}", username, roles);

        ServerHttpRequest.Builder builder = request.mutate()
                .header("X-Usuario", username);

        if (roles != null && !roles.isEmpty()) {
            builder.header("X-Usuario-Rol", String.join(",", roles));
        }

        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    /**
     * Verifica si la ruta es pública y no requiere autenticación.
     *
     * @param path la ruta solicitada
     * @return true si la ruta es pública, false en caso contrario
     */
    private boolean esRutaPublica(String path) {
        String[] rutasPublicas = {
            "/auth/**"
        };
        for (String patron : rutasPublicas) {
            if (coincideRuta(path, patron)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Comprueba si la ruta coincide con el patrón especificado. Soporta
     * patrones con /** al final para coincidencia por prefijo.
     *
     * @param path la ruta a comparar
     * @param patron el patrón de ruta (ej: /auth/**)
     * @return true si coinciden, false en caso contrario
     */
    private boolean coincideRuta(String path, String patron) {
        if (patron.equals("/**")) {
            return true;
        }
        if (patron.endsWith("/**")) {
            String prefijo = patron.substring(0, patron.length() - 3);
            return path.startsWith(prefijo);
        }
        return path.equals(patron);
    }

    /**
     * Define el orden de ejecución del filtro. Se ejecuta con la máxima
     * prioridad para validar antes que otros filtros.
     *
     * @return el orden de prioridad del filtro
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
