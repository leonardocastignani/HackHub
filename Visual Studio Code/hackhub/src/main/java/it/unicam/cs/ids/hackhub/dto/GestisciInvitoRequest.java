package it.unicam.cs.ids.hackhub.dto;

public record GestisciInvitoRequest(
    String azione,
    String codiceFiscaleUtenteLoggato
) {}