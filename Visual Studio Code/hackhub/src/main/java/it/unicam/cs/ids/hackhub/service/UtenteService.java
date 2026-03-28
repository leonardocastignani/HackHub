package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;

    public UtenteService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    public boolean esisteEmail(String email) {
        return utenteRepository.existsByEmail(email);
    }

    public boolean esisteCodiceFiscale(String codiceFiscale) {
        return utenteRepository.existsById(codiceFiscale);
    }

    public Utente salvaUtente(Utente utente) {
        return utenteRepository.save(utente);
    }

    public Optional<Utente> trovaPerEmail(String email) {
        return utenteRepository.findByEmail(email);
    }

    public Optional<Utente> trovaPerCodiceFiscale(String codiceFiscale) {
        return utenteRepository.findById(codiceFiscale);
    }
}