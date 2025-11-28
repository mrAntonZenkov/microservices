package org.example.orderservice.dto.errordto;

import java.time.ZonedDateTime;

public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message
) {}
