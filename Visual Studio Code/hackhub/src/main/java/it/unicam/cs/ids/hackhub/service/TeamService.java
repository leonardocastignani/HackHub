package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;

import java.util.Optional;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private HackathonRepository hackathonRepository;
    @Autowired
    private UtenteRepository utenteRepository;

    public Team creaTeam(Team team, Long hackathonId, Long ownerId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
            .orElseThrow(() -> new RuntimeException("Hackathon non trovato"));

        Utente owner = utenteRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Utente owner non trovato"));

        team.setHackathon(hackathon);
        team.setOwner(owner);

        return teamRepository.save(team);
    }

    public Optional<Team> ottieniTeam(Long id) {
        return teamRepository.findById(id);
    }
}