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
    public ResponseEntity<?> visualizzaSottomissioniDaValutare(@PathVariable Long idHackathon, @RequestParam String codiceFiscaleGiudice) {
        List<Sottomissione> sottomissioni = hackHubSystem.visualizzaSottomissioniDaValutare(
            idHackathon, 
            codiceFiscaleGiudice
        );

        if (sottomissioni.isEmpty()) {
            return ResponseEntity.ok("Non ci sono sottomissioni pronte per essere valutate");
        }
            
        return ResponseEntity.ok(sottomissioni);
    }

    // ===============================
    // ENDPOINT: Valuta Sottomissione
    // ===============================
    @PostMapping("/sottomissione/{idSottomissione}")
    public ResponseEntity<?> valutaSottomissione(@PathVariable Long idSottomissione, @RequestBody ValutaSottomissioneRequest request) {
        Valutazione valutazione = hackHubSystem.valutaSottomissione(
            idSottomissione,
            request.punteggio(),
            request.commento(),
            request.codiceFiscaleGiudice()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(valutazione);
    }

    // ==========================================
    // ENDPOINT: Visualizza Valutazione Ricevuta
    // ==========================================
    @GetMapping("/ricevute/{idTeam}")
    public ResponseEntity<?> visualizzaValutazioniRicevute(@PathVariable Long idTeam, @RequestParam String codiceFiscaleRichiedente) {
        List<Valutazione> valutazioni = this.hackHubSystem.visualizzaValutazione(idTeam, codiceFiscaleRichiedente);

        if (valutazioni.isEmpty()) {
            return ResponseEntity.ok("Nessuna valutazione ricevuta al momento.");
        }
            
        return ResponseEntity.ok(valutazioni);
    }
}