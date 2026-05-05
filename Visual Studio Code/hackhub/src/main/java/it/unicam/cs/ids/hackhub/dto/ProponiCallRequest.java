package it.unicam.cs.ids.hackhub.dto;

import java.time.LocalDateTime;

public record ProponiCallRequest(
    Long idTeam,
    LocalDateTime dataOra,
    int durataMinuti,
    String codiceFiscaleMentore
) {}