package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.time.LocalDate;
import java.util.*;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UtenteRepository utenteRepository;

    public Team creaTeam(Team team, Long ownerId) {
        Utente owner = utenteRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Utente owner non trovato"));

        team.setOwner(owner);

        if (team.getDataCreazione() == null) {
            team.setDataCreazione(LocalDate.now());
        }

        if (team.getNumeroMembri() <= 0) {
            team.setNumeroMembri(1);
        }

        return teamRepository.save(team);
    }

    public Optional<Team> ottieniTeam(Long id) {
        return teamRepository.findById(id);
    }
}