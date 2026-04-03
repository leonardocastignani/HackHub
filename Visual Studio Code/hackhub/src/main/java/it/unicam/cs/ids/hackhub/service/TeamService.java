package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Optional<Team> trovaTeamPerId(Long id) {
        return this.teamRepository.findById(id);
    }

    public boolean esisteNomeTeam(String nomeTeam) {
        return this.teamRepository.existsByNomeTeam(nomeTeam);
    }

    public Team salvaTeam(Team team) {
        return this.teamRepository.save(team);
    }
}