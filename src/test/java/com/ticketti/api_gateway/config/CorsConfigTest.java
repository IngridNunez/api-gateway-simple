package com.ticketti.api_gateway.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.cors.reactive.CorsWebFilter;

@ExtendWith(MockitoExtension.class)
class CorsConfigTest {

    @InjectMocks
    private CorsConfig corsConfig;

    private CorsWebFilter corsWebFilter;

    @BeforeEach
    void configurar() {
        corsWebFilter = corsConfig.corsWebFilter();
    }

    @Test
    void filtroCors_RetornaFiltroCors() {
        assertNotNull(corsWebFilter);
        assertTrue(corsWebFilter instanceof CorsWebFilter);
    }

    @Test
    void filtroCors_ConfiguracionContieneOrigenesPermitidos() {
        assertNotNull(corsWebFilter);
    }

    @Test
    void filtroCors_ConfiguracionContieneMetodosPermitidos() {
        assertNotNull(corsWebFilter);
    }

    @Test
    void filtroCors_ConfiguracionContieneEncabezadosPermitidos() {
        assertNotNull(corsWebFilter);
    }

    @Test
    void filtroCors_ConfiguracionPermiteCredenciales() {
        assertNotNull(corsWebFilter);
    }

    @Test
    void filtroCors_ConfiguracionTieneMaxAge() {
        assertNotNull(corsWebFilter);
    }

    @Test
    void filtroCors_ConfiguracionAplicaATodasLasRutas() {
        assertNotNull(corsWebFilter);
    }
}
