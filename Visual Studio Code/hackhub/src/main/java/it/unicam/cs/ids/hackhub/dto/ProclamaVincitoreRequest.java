package it.unicam.cs.ids.hackhub.dto;

public record ProclamaVincitoreRequest(
    Long idTeamVincitore,
    String codiceFiscaleOrganizzatore
) {}