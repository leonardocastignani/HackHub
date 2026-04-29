package it.unicam.cs.ids.hackhub.dto;

public record AggiornaSottomissioneRequest(
    String nuovoLink, 
    String codiceFiscaleRichiedente
) {}