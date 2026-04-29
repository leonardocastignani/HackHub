package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.dto.*;
import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/valutazioni")
public class ValutazioneController {

    private final HackHubSystem hackHubSystem;

    public ValutazioneController(HackHubSystem hackHubSystem) {
        this.hackHubSystem = hackHubSystem;
    }

    // ===============================================
    // ENDPOINT: Visualizza Sottomissioni Da Valutare
    // ===============================================
    @GetMapping("/hackathon/{idHackathon}")
    public ResponseEntity<?> visualizzaSottomissioniDaValutare(
            @PathVariable Long idHackathon, 
            @RequestParam String codiceFiscaleGiudice) {
        try {
            List<Sottomissione> sottomissioni = hackHubSystem.visualizzaSottomissioniDaValutare(
                    idHackathon, 
                    codiceFiscaleGiudice
            );

            if (sottomissioni.isEmpty()) {
                return ResponseEntity.ok("Non ci sono sottomissioni pronte per essere valutate");
            }
            
            return ResponseEntity.ok(sottomissioni);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("Permesso negato")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ===============================
    // ENDPOINT: Valuta Sottomissione
    // ===============================
    @PostMapping("/sottomissione/{idSottomissione}")
    public ResponseEntity<?> valutaSottomissione(
            @PathVariable Long idSottomissione, 
            @RequestBody ValutaSottomissioneRequest request) {
        try {
            Valutazione valutazione = hackHubSystem.valutaSottomissione(
                    idSottomissione,
                    request.punteggio(),
                    request.commento(),
                    request.codiceFiscaleGiudice()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(valutazione);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // ==========================================
    // ENDPOINT: Visualizza Valutazione Ricevuta
    // ==========================================
    @GetMapping("/ricevute/{idSottomissione}")
    public ResponseEntity<?> visualizzaValutazioniRicevute(
            @PathVariable Long idSottomissione, 
            @RequestParam String codiceFiscaleRichiedente) {
        try {
            List<Valutazione> valutazioni = this.hackHubSystem.visualizzaValutazioniRicevute(idSottomissione, codiceFiscaleRichiedente);

            if (valutazioni.isEmpty()) {
                return ResponseEntity.ok("Nessuna valutazione ricevuta al momento.");
            }
            
            return ResponseEntity.ok(valutazioni);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("termine dell'Hackathon")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}