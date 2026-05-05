package it.unicam.cs.ids.hackhub.dto;

public record SegnalaViolazioneRequest(
    Long idTeam,
    String motivazione,
    String codiceFiscaleMentore
) {}