package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class OrganizzatoreService {

    private final OrganizzatoreRepository organizzatoreRepository;

    public OrganizzatoreService(OrganizzatoreRepository organizzatoreRepository) {
        this.organizzatoreRepository = organizzatoreRepository;
    }

    public Optional<Organizzatore> trovaPerCodiceFiscale(String codiceFiscale) {
        return this.organizzatoreRepository.findById(codiceFiscale);
    }
}