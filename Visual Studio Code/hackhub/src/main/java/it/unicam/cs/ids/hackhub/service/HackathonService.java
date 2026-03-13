package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class HackathonService {

    @Autowired
    private HackathonRepository hackathonRepository;

    public Optional<Hackathon> ottieniHackathon(Long id) {
        return hackathonRepository.findById(id);
    }

    public List<Hackathon> ottieniTuttiHackathon() {
        return hackathonRepository.findAll();
    }

    public Hackathon creaHackathon(Hackathon hackathon) {
        // Qui potresti aggiungere controlli (es. data fine > data inizio)
        return hackathonRepository.save(hackathon);
    }
}