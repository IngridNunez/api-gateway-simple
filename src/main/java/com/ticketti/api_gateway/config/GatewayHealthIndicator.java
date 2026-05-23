package com.ticketti.api_gateway.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;

@Component
public class GatewayHealthIndicator implements HealthIndicator {

    private final DiscoveryClient discoveryClient;

    public GatewayHealthIndicator(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public Health health() {
        List<String> services = discoveryClient.getServices();
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("servicesCount", services.size());
        details.put("services", services);

        if (services.isEmpty()) {
            return Health.down()
                    .withDetails(details)
                    .withDetail("message", "No hay servicios registrados en Eureka")
                    .build();
        }

        return Health.up()
                .withDetails(details)
                .build();
    }
}