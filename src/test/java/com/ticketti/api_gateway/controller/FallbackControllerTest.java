package com.ticketti.api_gateway.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class FallbackControllerTest {

    @InjectMocks
    private FallbackController fallbackController;

    @Test
    void fallbackAuth_RetornaRespuestaServicioNoDisponible() {
        ResponseEntity<Map<String, String>> response = fallbackController.authFallback();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Service Unavailable", response.getBody().get("error"));
        assertEquals("El servicio de autenticación no está disponible. Intente más tarde.", response.getBody().get("message"));
    }

    @Test
    void fallbackCarrito_RetornaRespuestaServicioNoDisponible() {
        ResponseEntity<Map<String, String>> response = fallbackController.carritoFallback();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Service Unavailable", response.getBody().get("error"));
        assertEquals("El servicio de carrito no está disponible. Intente más tarde.", response.getBody().get("message"));
    }

    @Test
    void fallbackUsuarios_RetornaRespuestaServicioNoDisponible() {
        ResponseEntity<Map<String, String>> response = fallbackController.usuariosFallback();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Service Unavailable", response.getBody().get("error"));
        assertEquals("El servicio de usuarios no está disponible. Intente más tarde.", response.getBody().get("message"));
    }

    @Test
    void fallbackEventos_RetornaRespuestaServicioNoDisponible() {
        ResponseEntity<Map<String, String>> response = fallbackController.eventosFallback();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Service Unavailable", response.getBody().get("error"));
        assertEquals("El servicio de eventos no está disponible. Intente más tarde.", response.getBody().get("message"));
    }

    @Test
    void fallbackDonaciones_RetornaRespuestaServicioNoDisponible() {
        ResponseEntity<Map<String, String>> response = fallbackController.donacionesFallback();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Service Unavailable", response.getBody().get("error"));
        assertEquals("El servicio de donaciones no está disponible. Intente más tarde.", response.getBody().get("message"));
    }

    @Test
    void fallbackMensajeria_RetornaRespuestaServicioNoDisponible() {
        ResponseEntity<Map<String, String>> response = fallbackController.mensajeriaFallback();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Service Unavailable", response.getBody().get("error"));
        assertEquals("El servicio de mensajería no está disponible. Intente más tarde.", response.getBody().get("message"));
    }

    @Test
    void fallbackGenerico_RetornaRespuestaServicioNoDisponible() {
        ResponseEntity<Map<String, String>> response = fallbackController.genericFallback();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Service Unavailable", response.getBody().get("error"));
        assertEquals("El servicio no está disponible temporalmente. Intente más tarde.", response.getBody().get("message"));
    }

    @Test
    void todosLosMetodosFallback_RetornanHttpStatusCorrecto() {
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, fallbackController.authFallback().getStatusCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, fallbackController.carritoFallback().getStatusCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, fallbackController.usuariosFallback().getStatusCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, fallbackController.eventosFallback().getStatusCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, fallbackController.donacionesFallback().getStatusCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, fallbackController.mensajeriaFallback().getStatusCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, fallbackController.genericFallback().getStatusCode());
    }

    @Test
    void todosLosMetodosFallback_RetornanCuerpoNoVacio() {
        assertNotNull(fallbackController.authFallback().getBody());
        assertNotNull(fallbackController.carritoFallback().getBody());
        assertNotNull(fallbackController.usuariosFallback().getBody());
        assertNotNull(fallbackController.eventosFallback().getBody());
        assertNotNull(fallbackController.donacionesFallback().getBody());
        assertNotNull(fallbackController.mensajeriaFallback().getBody());
        assertNotNull(fallbackController.genericFallback().getBody());
    }

    @Test
    void todosLosMetodosFallback_ContienenCampoError() {
        assertTrue(fallbackController.authFallback().getBody().containsKey("error"));
        assertTrue(fallbackController.carritoFallback().getBody().containsKey("error"));
        assertTrue(fallbackController.usuariosFallback().getBody().containsKey("error"));
        assertTrue(fallbackController.eventosFallback().getBody().containsKey("error"));
        assertTrue(fallbackController.donacionesFallback().getBody().containsKey("error"));
        assertTrue(fallbackController.mensajeriaFallback().getBody().containsKey("error"));
        assertTrue(fallbackController.genericFallback().getBody().containsKey("error"));
    }

    @Test
    void todosLosMetodosFallback_ContienenCampoMensaje() {
        assertTrue(fallbackController.authFallback().getBody().containsKey("message"));
        assertTrue(fallbackController.carritoFallback().getBody().containsKey("message"));
        assertTrue(fallbackController.usuariosFallback().getBody().containsKey("message"));
        assertTrue(fallbackController.eventosFallback().getBody().containsKey("message"));
        assertTrue(fallbackController.donacionesFallback().getBody().containsKey("message"));
        assertTrue(fallbackController.mensajeriaFallback().getBody().containsKey("message"));
        assertTrue(fallbackController.genericFallback().getBody().containsKey("message"));
    }
}
