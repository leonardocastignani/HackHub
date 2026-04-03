package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.dto.*;
import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final HackHubSystem hackHubSystem;

    public TeamController(HackHubSystem hackHubSystem) {
        this.hackHubSystem = hackHubSystem;
    }

    // ==========================
    // ENDPOINT: Crea Nuovo Team
    // ==========================
    @PostMapping("/crea")
    public ResponseEntity<?> creaTeam(@RequestBody CreaTeamRequest request) {
        try {
            Team nuovoTeam = this.hackHubSystem.creaTeam(request.nomeTeam(), request.ownerCodiceFiscale());
            return ResponseEntity.status(HttpStatus.CREATED).body(nuovoTeam);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore tecnico durante la creazione del team.");
        }
    }

    // ===================================
    // ENDPOINT: Visualizza Dettagli Team
    // ===================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getDettagliTeam(@PathVariable Long id) {
        try {
            Team team = this.hackHubSystem.visualizzaDettagliTeam(id);
            return ResponseEntity.ok(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // =================================
    // ENDPOINT: Iscrivi Team Hackathon
    // =================================
    @PostMapping("/{idTeam}/iscrivi/{idHackathon}")
    public ResponseEntity<?> iscriviTeamHackathon(
            @PathVariable Long idTeam, 
            @PathVariable Long idHackathon, 
            @RequestBody IscriviTeamRequest request) {
        try {
            Team teamIscritto = this.hackHubSystem.iscriviTeamHackathon(
                    idTeam, 
                    idHackathon, 
                    request.codiceFiscaleRichiedente()
            );
            return ResponseEntity.ok(teamIscritto);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}