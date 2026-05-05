package it.unicam.cs.ids.hackhub.service;

import org.springframework.stereotype.*;
import java.time.*;
import java.util.*;
import org.slf4j.*;

@Service
public class SistemaCalendarEsterno {

    private static final Logger logger = LoggerFactory.getLogger(SistemaCalendarEsterno.class);

    public record RispostaCalendar(boolean successo, String idCallEsterno, String linkMeet, String errore) {}

    public RispostaCalendar prenotaSlot(LocalDateTime dataOraRichiesta, int durataMinuti) {
        double probabilitaSuccesso = Math.random();

        if (probabilitaSuccesso > 0.15) {
            String fakeIdCall = "CAL-" + UUID.randomUUID().toString().substring(0, 8);
            String fakeMeetLink = "https://meet.google.com/" + UUID.randomUUID().toString().substring(0, 12);
            return new RispostaCalendar(true, fakeIdCall, fakeMeetLink, null);
        } else {
            String erroreMsg = "Timeout o slot non disponibile sul server Calendar.";
            logger.error("ERRORE PRENOTAZIONE CALENDAR: " + erroreMsg + " per data/ora " + dataOraRichiesta);
            return new RispostaCalendar(false, null, null, erroreMsg);
        }
    }

    public boolean annullaSlot(String idCallEsterno) {
        if (idCallEsterno == null || idCallEsterno.isEmpty()) {
            return false;
        }
        
        double probabilitaSuccesso = Math.random();

        if (probabilitaSuccesso > 0.15) {
            return true;
        } else {
            logger.error("ERRORE CANCELLAZIONE CALENDAR: Timeout o errore per l'evento ID " + idCallEsterno);
            return false;
        }
    }
}