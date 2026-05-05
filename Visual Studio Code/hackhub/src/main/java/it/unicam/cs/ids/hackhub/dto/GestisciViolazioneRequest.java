package it.unicam.cs.ids.hackhub.dto;

import it.unicam.cs.ids.hackhub.model.enums.*;

public record GestisciViolazioneRequest(
    String decisione,
    EsitoViolazione esito,
    String codiceFiscaleOrganizzatore
) {}