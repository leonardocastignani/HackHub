package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private HackathonRepository hackathonRepository;
    @Autowired
    private UtenteRepository utenteRepository;

    public Team creaTeam(Team team, Long hackathonId, Long ownerId) {
        // 1. Recupera l'Hackathon
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
            .orElseThrow(() -> new RuntimeException("Hackathon non trovato"));

        // 2. Recupera l'Owner
        Utente owner = utenteRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Utente owner non trovato"));

        // 3. Imposta le relazioni
        team.setHackathon(hackathon);
        team.setOwner(owner);
        
        // 4. Salva il team
        return teamRepository.save(team);
    }
}