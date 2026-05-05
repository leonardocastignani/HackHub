package it.unicam.cs.ids.hackhub.service;

import org.springframework.stereotype.*;
import java.util.*;

@Service
public class SistemaPagamentoEsterno {

    public record EsitoTransazione(boolean successo, String idTransazione, String errore) {}

    public EsitoTransazione processaPagamento(Double importo, String identificativoDestinatario) {
        double probabilitaSuccesso = Math.random();

        if (probabilitaSuccesso > 0.2) {
            String transazioneEsterna = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            return new EsitoTransazione(true, transazioneEsterna, null);
        } else {
            return new EsitoTransazione(false, null, "Fondi insufficienti o coordinate bancarie del team non valide.");
        }
    }
}