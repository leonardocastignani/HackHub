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
        return hackathonRepository.findAll();
    }

    public Optional<Hackathon> ottieniDettagliHackathon(Long id) {
        return hackathonRepository.findById(id);
    }
}