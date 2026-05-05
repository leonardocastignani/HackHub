package it.unicam.cs.ids.hackhub.dto;

import it.unicam.cs.ids.hackhub.model.enums.*;

public record GestisciInvitoRequest(
    AzioneInvito azione,
    String codiceFiscaleUtenteLoggato
) {}