package it.unicam.cs.ids.hackhub.dto;

import java.time.*;

public record CreaHackathonRequest(
    String nome, 
    String descrizione, 
    LocalDate dataInizio, 
    LocalDate dataFine, 
    String codiceFiscaleOrganizzatore
) {}