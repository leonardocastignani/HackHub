package it.unicam.cs.ids.hackhub.dto;

public record CaricaSottomissioneRequest(
    Long idTeam, 
    String linkProgetto, 
    String codiceFiscaleRichiedente
) {}