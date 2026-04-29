package it.unicam.cs.ids.hackhub.dto;

public record InviaRichiestaSupportoRequest(
    String messaggio,
    String codiceFiscaleRichiedente
) {}