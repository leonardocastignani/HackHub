package it.unicam.cs.ids.hackhub.dto;

public record NominaMentoreRequest(
    Long idHackathon,
    String codiceFiscaleNuovoMentore,
    String codiceFiscaleVecchioMentore,
    String codiceFiscaleOrganizzatore
) {}