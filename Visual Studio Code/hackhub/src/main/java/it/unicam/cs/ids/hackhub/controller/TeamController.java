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
        Team nuovoTeam = this.hackHubSystem.creaTeam(request.nomeTeam(), request.ownerCodiceFiscale());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuovoTeam);
    }

    // ===================================
    // ENDPOINT: Visualizza Dettagli Team
    // ===================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getDettagliTeam(@PathVariable Long id, @RequestParam(required = false) String codiceFiscaleRichiedente) {
        Team team = this.hackHubSystem.visualizzaDettagliTeam(id, codiceFiscaleRichiedente);
        
        return ResponseEntity.ok(team);
    }

    // =================================
    // ENDPOINT: Iscrivi Team Hackathon
    // =================================
    @PostMapping("/{idTeam}/iscrivi/{idHackathon}")
    public ResponseEntity<?> iscriviTeamHackathon(@PathVariable Long idTeam, @PathVariable Long idHackathon, @RequestBody IscriviTeamRequest request) {
        Team teamIscritto = this.hackHubSystem.iscriviTeamHackathon(
            idTeam, 
            idHackathon, 
            request.codiceFiscaleRichiedente()
        );

        return ResponseEntity.ok(teamIscritto);
    }
}