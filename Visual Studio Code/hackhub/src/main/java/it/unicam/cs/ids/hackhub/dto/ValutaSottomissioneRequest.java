package it.unicam.cs.ids.hackhub.dto;

public record ValutaSottomissioneRequest(
    int punteggio,
    String commento,
    String codiceFiscaleGiudice
) {}