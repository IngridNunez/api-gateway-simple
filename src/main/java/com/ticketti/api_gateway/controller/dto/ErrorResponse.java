package com.ticketti.api_gateway.controller.dto;

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