package it.unicam.cs.ids.hackhub.dto;

public record RegistrazioneRequest(
    String codiceFiscale, 
    String nome, 
    String cognome, 
    String email, 
    String password
) {}