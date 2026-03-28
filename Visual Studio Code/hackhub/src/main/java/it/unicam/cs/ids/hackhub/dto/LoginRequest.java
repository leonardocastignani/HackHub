package it.unicam.cs.ids.hackhub.dto;

public record LoginRequest(
    String email, 
    String password
) {}