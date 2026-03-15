package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    // TODO aggiornare: diversi tipi di fallimenti
    public Utente registraUtente(Utente utente) {
        if (utenteRepository.findByEmail(utente.getEmail()).isPresent()) {
            throw new RuntimeException("Email già in uso!");
        }
        return utenteRepository.save(utente);
    }

    // TODO aggiornare: diversi tipi di fallimenti
    public Utente login(String email, String password) {
        Optional<Utente> utente = utenteRepository.findByEmail(email);
        
        if (utente.isPresent() && utente.get().getPassword().equals(password)) {
             return utente.get();
        } else {
             throw new RuntimeException("Credenziali non valide");
        }
    }
}