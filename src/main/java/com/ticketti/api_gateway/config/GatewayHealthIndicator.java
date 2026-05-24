package com.ticketti.api_gateway.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

/**
 * Indicador de salud personalizado que verifica la conectividad con Eureka
 * y lista los servicios registrados disponibles en el gateway.
 */
@Component
public class GatewayHealthIndicator implements HealthIndicator {

    /**
     * Inyecta el cliente de descubrimiento para consultar servicios registrados.
     *
     * @param discoveryClient cliente de descubrimiento de Eureka
     */
    public GatewayHealthIndicator(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * Obtiene el estado de salud del gateway consultando los servicios registrados en Eureka.
     *
     * @return estado UP si hay servicios registrados, DOWN en caso contrario
     */
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
