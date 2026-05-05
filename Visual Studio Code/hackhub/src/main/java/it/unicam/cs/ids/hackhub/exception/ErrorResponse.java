package it.unicam.cs.ids.hackhub.exception;

import java.time.*; 

public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {}