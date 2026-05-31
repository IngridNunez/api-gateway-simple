package com.ticketti.api_gateway.controller.dto;

/**
 * DTO que representa una respuesta de error estructurada.
 * Se utiliza en los endpoints de fallback y para errores consistentes.
 *
 * @param timestamp fecha y hora del error en formato ISO 8601
 * @param path ruta del request que generó el error
 * @param status código de estado HTTP
 * @param error descripción corta del error
 * @param service nombre del servicio afectado
 * @param message mensaje amigable al usuario
 * @param requestId identificador de solicitud para trazabilidad
 */
public record ErrorResponse(
        String timestamp,
        String path,
        int status,
        String error,
        String service,
        String message,
        String requestId
) {
}
