package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

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

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeam(@PathVariable Long id) {
        return teamService.ottieniTeam(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}