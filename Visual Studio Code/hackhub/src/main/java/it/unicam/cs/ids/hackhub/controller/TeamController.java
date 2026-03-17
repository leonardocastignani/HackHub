package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.model.Team;
import it.unicam.cs.ids.hackhub.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    // Esempio chiamata: POST /api/teams?hackathonId=1&ownerId=5
    // Body: { "nomeTeam": "SuperCoders", "numeroMembri": 4 }
    @PostMapping
    public ResponseEntity<?> creaNuovoTeam(
            @RequestBody Team team, 
            @RequestParam Long hackathonId, 
            @RequestParam Long ownerId) {
        try {
            Team nuovoTeam = teamService.creaTeam(team, hackathonId, ownerId);
            return ResponseEntity.ok(nuovoTeam);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}