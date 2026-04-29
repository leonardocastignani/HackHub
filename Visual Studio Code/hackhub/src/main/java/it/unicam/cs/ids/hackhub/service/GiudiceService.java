package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class GiudiceService {

    private final GiudiceRepository giudiceRepository;

    public GiudiceService(GiudiceRepository giudiceRepository) {
        this.giudiceRepository = giudiceRepository;
    }

    public Optional<Giudice> trovaPerCodiceFiscale(String codiceFiscale) {
        return giudiceRepository.findById(codiceFiscale);
    }
}