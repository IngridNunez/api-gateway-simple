package com.ticketti.api_gateway.controller;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketti.api_gateway.controller.dto.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Controlador que maneja las respuestas de fallback cuando un microservicio no
 * está disponible mediante el Circuit Breaker.
 */
@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final String SERVICE_UNAVAILABLE = "Service Unavailable";
    private static final String HEADER_RETRY_AFTER = "Retry-After";
    private static final String HEADER_X_REQUEST_ID = "X-Request-ID";

    /**
     * Maneja las respuestas de fallback cuando un servicio no está disponible.
     * Devuelve una respuesta estructurada con el estado 503 (Service
     * Unavailable), headers de reintento y trazabilidad.
     *
     * @param service nombre del servicio que no está disponible
     * @param request solicitud HTTP original
     * @return respuesta de error con cuerpo ErrorResponse y headers apropiados
     */
    @RequestMapping("/{service}")
    public ResponseEntity<ErrorResponse> fallback(@PathVariable String service, ServerHttpRequest request) {
        String requestId = request.getHeaders().getFirst(HEADER_X_REQUEST_ID);
        String message = obtenerMensaje(service);

        log.error("Service {} is unavailable, requestId={}", service, requestId);

        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now().toString(),
                request.getPath().value(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                SERVICE_UNAVAILABLE,
                service,
                message,
                requestId
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header(HEADER_RETRY_AFTER, "30")
                .header(HEADER_X_REQUEST_ID, requestId != null ? requestId : "unknown")
                .body(errorResponse);
    }

    /**
     * Obtiene el mensaje de error amigable según el nombre del servicio.
     *
     * @param service nombre del servicio para el cual se requiere el mensaje
     * @return mensaje de error descriptivo para el servicio
     */
    private String obtenerMensaje(String service) {
        return switch (service) {
            case "auth" ->
                "El servicio de autenticación no está disponible. Intente más tarde.";
            case "carrito" ->
                "El servicio de carrito no está disponible. Intente más tarde.";
            case "usuarios" ->
                "El servicio de usuarios no está disponible. Intente más tarde.";
            case "eventos" ->
                "El servicio de eventos no está disponible. Intente más tarde.";
            case "donaciones" ->
                "El servicio de donaciones no está disponible. Intente más tarde.";
            case "mensajeria" ->
                "El servicio de mensajería no está disponible. Intente más tarde.";
            default ->
                "El servicio no está disponible temporalmente. Intente más tarde.";
        };
    }
}
