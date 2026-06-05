package com.ticketti.api_gateway.filter;

import java.util.UUID;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Filtro global que asigna un identificador único de solicitud (X-Request-ID)
 * para permitir la trazabilidad a través de los microservicios.
 * Si la solicitud entrante ya incluye el header, lo propaga; de lo contrario,
 * genera un UUID nuevo.
 */
@Slf4j
@Component
public class RequestIdGlobalFilter implements GlobalFilter, Ordered {

    /**
     * Genera o propaga un identificador de solicitud para trazabilidad.
     * Si la solicitud entrante no tiene el encabezado X-Request-ID, se genera
     * un nuevo UUID. El identificador se agrega tanto a la solicitud como a la
     * respuesta para su seguimiento.
     *
     * @param exchange intercambio del servidor web
     * @param chain cadena de filtros del gateway
     * @return Mono que completa el procesamiento de la solicitud
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = request.getHeaders().getFirst("X-Request-ID");

        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(request.mutate().header("X-Request-ID", requestId).build())
                .build();

        mutatedExchange.getResponse().getHeaders().set("X-Request-ID", requestId);
        log.debug("Request ID asignado: {}", requestId);

        return chain.filter(mutatedExchange);
    }

    /**
     * Define el orden de ejecución de este filtro.
     * Se establece con la máxima precedencia para asegurar que el identificador
     * de solicitud se asigne antes que cualquier otro procesamiento.
     *
     * @return orden de precedencia del filtro
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
