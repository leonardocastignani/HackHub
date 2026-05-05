package it.unicam.cs.ids.hackhub.dto;

public record AvviaPagamentoRequest(
    Long idHackathon,
    String codiceFiscaleOrganizzatore
) {}