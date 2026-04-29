package it.unicam.cs.ids.hackhub.dto;

public record NominaGiudiceRequest(
    Long idHackathon,
    String codiceFiscaleUtente,
    String codiceFiscaleOrganizzatore
) {}