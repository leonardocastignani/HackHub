package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.dto.*;
import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/violazioni")
public class ViolazioneController {

    private final HackHubSystem hackHubSystem;

    public ViolazioneController(HackHubSystem hackHubSystem) {
        this.hackHubSystem = hackHubSystem;
    }

    // =============================
    // ENDPOINT: Segnala Violazione
    // =============================
    @PostMapping("/segnala")
    public ResponseEntity<?> segnalaViolazione(@RequestBody SegnalaViolazioneRequest request) {
        Violazione nuovaViolazione = this.hackHubSystem.segnalaViolazione(
            request.idTeam(),
            request.motivazione(),
            request.codiceFiscaleMentore()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuovaViolazione);
    }

    // ==============================
    // ENDPOINT: Gestisci Violazione
    // ==============================
    @PutMapping("/{id}/gestisci")
    public ResponseEntity<?> gestisciViolazione(@PathVariable Long id, @RequestBody GestisciViolazioneRequest request) {
        Violazione violazioneGestita = this.hackHubSystem.gestisciViolazione(
            id,
            request.decisione(),
            request.esito(),
            request.codiceFiscaleOrganizzatore()
        );
        
        return ResponseEntity.ok(violazioneGestita);
    }
}