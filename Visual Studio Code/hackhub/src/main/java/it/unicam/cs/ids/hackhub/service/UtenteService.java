package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.stereotype.*;
import java.time.*;
import java.util.*;

@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;

    public UtenteService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    public boolean esisteEmail(String email) {
        return this.utenteRepository.existsByEmail(email);
    }

    public boolean esisteCodiceFiscale(String codiceFiscale) {
        return this.utenteRepository.existsById(codiceFiscale);
    }

    public Utente salvaUtente(Utente utente) {
        return this.utenteRepository.save(utente);
    }

    public Optional<Utente> trovaPerEmail(String email) {
        return this.utenteRepository.findByEmail(email);
    }

    public Optional<Utente> trovaPerCodiceFiscale(String codiceFiscale) {
        return this.utenteRepository.findById(codiceFiscale);
    }

    public void promuoviAMembroDelTeam(String codiceFiscale, Long teamId) {
        this.utenteRepository.promuoviAMembroDelTeam(codiceFiscale, teamId, LocalDate.now());
    }
}