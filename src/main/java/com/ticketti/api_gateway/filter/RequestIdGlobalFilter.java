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

@Slf4j
@Component
public class RequestIdGlobalFilter implements GlobalFilter, Ordered {

    /**
     * Generates or propagates a request ID for tracing. If the incoming request
     * doesn't have an X-Request-ID header, a new UUID is generated. The request
     * ID is added to both the request headers and response headers for tracing
     * purposes.
     *
     * @param exchange the server web exchange
     * @param chain the gateway filter chain
     * @return Mono that completes when the request processing is done
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
     * Defines the execution order of this filter. Set to highest precedence to
     * ensure request ID is assigned before any other processing occurs.
     *
     * @return the order of precedence for this filter
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
