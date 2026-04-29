package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class HackathonService {

    private final HackathonRepository hackathonRepository;

    public HackathonService(HackathonRepository hackathonRepository) {
        this.hackathonRepository = hackathonRepository;
    }

    public Iterable<Hackathon> ottieniTuttiHackathon() {
        return this.hackathonRepository.findAll();
    }

    public Optional<Hackathon> ottieniDettagliHackathon(Long id) {
        return this.hackathonRepository.findById(id);
    }

    public Hackathon salvaHackathon(Hackathon hackathon) {
        return this.hackathonRepository.save(hackathon);
    }

    public void assegnaGiudiceAHackathon(Long idHackathon, String codiceFiscale) {
        hackathonRepository.assegnaGiudiceAHackathon(idHackathon, codiceFiscale);
    }

    public void aggiungiMentoreAHackathon(Long idHackathon, String codiceFiscale) {
        hackathonRepository.aggiungiMentoreAHackathon(idHackathon, codiceFiscale);
    }

    public void rimuoviMentoreDaHackathon(Long idHackathon, String codiceFiscale) {
        hackathonRepository.rimuoviMentoreDaHackathon(idHackathon, codiceFiscale);
    }
}