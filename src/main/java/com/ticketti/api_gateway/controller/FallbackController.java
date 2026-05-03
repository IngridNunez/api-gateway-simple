package com.ticketti.api_gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/auth")
    public ResponseEntity<Map<String, String>> authFallback() {
        log.error("Auth service is unavailable");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "El servicio de autenticación no está disponible. Intente más tarde."
                ));
    }

    @RequestMapping("/carrito")
    public ResponseEntity<Map<String, String>> carritoFallback() {
        log.error("Carrito service is unavailable");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "El servicio de carrito no está disponible. Intente más tarde."
                ));
    }

    @RequestMapping("/usuarios")
    public ResponseEntity<Map<String, String>> usuariosFallback() {
        log.error("Usuarios service is unavailable");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "El servicio de usuarios no está disponible. Intente más tarde."
                ));
    }

    @RequestMapping("/eventos")
    public ResponseEntity<Map<String, String>> eventosFallback() {
        log.error("Eventos service is unavailable");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "El servicio de eventos no está disponible. Intente más tarde."
                ));
    }

    @RequestMapping("/donaciones")
    public ResponseEntity<Map<String, String>> donacionesFallback() {
        log.error("Donaciones service is unavailable");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "El servicio de donaciones no está disponible. Intente más tarde."
                ));
    }

    @RequestMapping("/mensajeria")
    public ResponseEntity<Map<String, String>> mensajeriaFallback() {
        log.error("Mensajeria service is unavailable");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "El servicio de mensajería no está disponible. Intente más tarde."
                ));
    }

    @RequestMapping("/**")
    public ResponseEntity<Map<String, String>> genericFallback() {
        log.error("Unknown service is unavailable");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", "El servicio no está disponible temporalmente. Intente más tarde."
                ));
    }
}
