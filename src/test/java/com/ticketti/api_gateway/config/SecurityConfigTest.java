package com.ticketti.api_gateway.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void configuracionSeguridad_NoEsNula() {
        assertNotNull(securityConfig);
    }

    @Test
    void cadenaFiltrosSeguridad_AlLlamarse_RetornaCadenaFiltrosSeguridad() {
        ServerHttpSecurity http = ServerHttpSecurity.http();
        SecurityWebFilterChain filterChain = securityConfig.springSecurityFilterChain(http);

        assertNotNull(filterChain);
        assertTrue(filterChain instanceof SecurityWebFilterChain);
    }
}
