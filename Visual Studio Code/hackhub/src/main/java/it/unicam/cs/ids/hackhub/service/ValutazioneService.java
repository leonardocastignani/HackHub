package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.stereotype.*;

@Service
public class ValutazioneService {

    private final ValutazioneRepository valutazioneRepository;

    public ValutazioneService(ValutazioneRepository valutazioneRepository) {
        this.valutazioneRepository = valutazioneRepository;
    }

    public boolean esisteValutazione(Long idSottomissione, String codiceFiscaleGiudice) {
        return this.valutazioneRepository.existsBySottomissione_IdAndGiudice_CodiceFiscale(idSottomissione, codiceFiscaleGiudice);
    }

    public Valutazione salvaValutazione(Valutazione valutazione) {
        return valutazioneRepository.save(valutazione);
    }
}