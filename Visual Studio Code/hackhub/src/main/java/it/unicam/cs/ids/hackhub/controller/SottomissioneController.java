package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.dto.*;
import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/sottomissioni")
public class SottomissioneController {

    private final HackHubSystem hackHubSystem;

    public SottomissioneController(HackHubSystem hackHubSystem) {
        this.hackHubSystem = hackHubSystem;
    }

    // ===============================
    // ENDPOINT: Carica Sottomissione
    // ===============================
    @PostMapping("/carica")
    public ResponseEntity<?> caricaSottomissione(@RequestBody CaricaSottomissioneRequest request) {
        try {
            Sottomissione sottomissione = hackHubSystem.caricaSottomissione(
                    request.idTeam(),
                    request.linkProgetto(),
                    request.codiceFiscaleRichiedente()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(sottomissione);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // =================================
    // ENDPOINT: Aggiorna Sottomissione
    // =================================
    @PutMapping("/{id}/aggiorna")
    public ResponseEntity<?> aggiornaSottomissione(
            @PathVariable Long id, 
            @RequestBody AggiornaSottomissioneRequest request) {
        try {
            Sottomissione sottomissione = hackHubSystem.aggiornaSottomissione(
                    id,
                    request.nuovoLink(),
                    request.codiceFiscaleRichiedente()
            );
            return ResponseEntity.ok(sottomissione);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ====================================================
    // ENDPOINT: Visualizza Stato e Dettagli Sottomissione
    // ====================================================
    @GetMapping("/team/{idTeam}")
    public ResponseEntity<?> visualizzaSottomissioniTeam(
            @PathVariable Long idTeam, 
            @RequestParam(required = false) String codiceFiscaleRichiedente) {
        try {
            List<Sottomissione> sottomissioni = this.hackHubSystem.visualizzaSottomissioniTeam(idTeam, codiceFiscaleRichiedente);

            if (sottomissioni.isEmpty()) {
                return ResponseEntity.ok("Nessuna sottomissione trovata per il team specificato.");
            }
            
            return ResponseEntity.ok(sottomissioni);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}