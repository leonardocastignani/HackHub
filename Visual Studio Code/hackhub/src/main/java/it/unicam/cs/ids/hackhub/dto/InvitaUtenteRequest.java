package it.unicam.cs.ids.hackhub.dto;

public record InvitaUtenteRequest(
    String emailUtente, 
    Long idTeam, 
    String codiceFiscaleOwner
) {}