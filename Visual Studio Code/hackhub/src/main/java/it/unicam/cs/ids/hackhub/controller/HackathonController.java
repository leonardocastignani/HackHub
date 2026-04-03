package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.dto.*;
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
        return ResponseEntity.ok(this.hackHubSystem.visualizzaElencoHackathon());
    }

    // ========================================
    // ENDPOINT: Visualizza Dettagli Hackathon
    // ========================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getDettagliHackathon(@PathVariable Long id) {
        try {
            Hackathon hackathon = this.hackHubSystem.visualizzaDettagliHackathon(id);
            return ResponseEntity.ok(hackathon);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // =========================
    // ENDPOINT: Crea Hackathon
    // =========================
    @PostMapping("/crea")
    public ResponseEntity<?> creaHackathon(@RequestBody CreaHackathonRequest request) {
        try {
            Hackathon nuovoHackathon = this.hackHubSystem.creaHackathon(
                    request.nome(),
                    request.descrizione(),
                    request.dataInizio(),
                    request.dataFine(),
                    request.codiceFiscaleOrganizzatore()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(nuovoHackathon);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}