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
    public ResponseEntity<?> getElencoHackathon() {
        Iterable<Hackathon> hackathons = this.hackHubSystem.visualizzaElencoHackathon();

        if (!hackathons.iterator().hasNext()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Al momento non sono disponibili hackathon");
        }
        
        return ResponseEntity.ok(hackathons);
    }

    // ========================================
    // ENDPOINT: Visualizza Dettagli Hackathon
    // ========================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getDettagliHackathon(@PathVariable Long id, @RequestParam(required = false) String codiceFiscaleUtente) {
        HackathonDettagliResponse response = this.hackHubSystem.visualizzaDettagliHackathon(id, codiceFiscaleUtente);
        
        return ResponseEntity.ok(response);
    }

    // =========================
    // ENDPOINT: Crea Hackathon
    // =========================
    @PostMapping("/crea")
    public ResponseEntity<?> creaHackathon(@RequestBody CreaHackathonRequest request) {
        Hackathon nuovoHackathon = this.hackHubSystem.creaHackathon(
            request.nome(),
            request.descrizione(),
            request.luogo(),
            request.regolamento(),
            request.dimensioneMassimaTeam(),
            request.premio(),
            request.dataInizio(),
            request.dataFine(),
            request.scadenzaIscrizione(),
            request.codiceFiscaleOrganizzatore()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(nuovoHackathon);
    }

    // ==================================
    // ENDPOINT: Proclama Team Vincitore
    // ==================================
    @PutMapping("/{id}/proclama-vincitore")
    public ResponseEntity<?> proclamaVincitore(@PathVariable Long id, @RequestBody ProclamaVincitoreRequest request) {
        Hackathon hackathonConcluso = this.hackHubSystem.proclamaVincitore(
            id,
            request.idTeamVincitore(),
            request.codiceFiscaleOrganizzatore()
        );

        return ResponseEntity.ok(hackathonConcluso);
    }

    // ===================================
    // ENDPOINT: Aggiorna Stato Hackathon
    // ===================================
    @PutMapping("/{id}/stato")
    public ResponseEntity<?> aggiornaStatoHackathon(@PathVariable Long id, @RequestParam String nuovoStato, @RequestParam String codiceFiscaleOrganizzatore) {
        Hackathon hackathonAggiornato = this.hackHubSystem.aggiornaStatoHackathon(
            id, 
            nuovoStato, 
            codiceFiscaleOrganizzatore
        );
        
        return ResponseEntity.ok(hackathonAggiornato);
    }
}