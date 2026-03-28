package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hackathons")
public class HackathonController {

    private final HackHubSystem hackHubSystem;

    public HackathonController(HackHubSystem hackHubSystem) {
        this.hackHubSystem = hackHubSystem;
    }

    // ======================================
    // ENDPOINT: Visualizza Elenco Hackathon
    // ======================================
    @GetMapping
    public ResponseEntity<Iterable<Hackathon>> getElencoHackathon() {
        return ResponseEntity.ok(hackHubSystem.visualizzaElencoHackathon());
    }

    // ========================================
    // ENDPOINT: Visualizza Dettagli Hackathon
    // ========================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getDettagliHackathon(@PathVariable Long id) {
        try {
            Hackathon hackathon = hackHubSystem.visualizzaDettagliHackathon(id);
            return ResponseEntity.ok(hackathon);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}