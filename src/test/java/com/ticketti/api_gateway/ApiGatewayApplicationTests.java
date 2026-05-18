package com.ticketti.api_gateway;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiGatewayApplicationTests {

	@Test
	void contextoCarga() {
		assertTrue(true, "El contexto de prueba carga correctamente");
	}

	@Test
	void metodoPrincipal_AlLlamarse_IniciaAplicacion() {
		String[] args = new String[]{};
		assertDoesNotThrow(() -> ApiGatewayApplication.main(args));
	}
}
