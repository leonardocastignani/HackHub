package it.unicam.cs.ids.hackhub.dto;

public record CreaTeamRequest(
    String nomeTeam,
    String ownerCodiceFiscale
) {}