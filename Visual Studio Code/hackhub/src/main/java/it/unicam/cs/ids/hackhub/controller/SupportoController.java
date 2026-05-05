package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.dto.*;
import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/supporto")
public class SupportoController {

    private final HackHubSystem hackHubSystem;

    public SupportoController(HackHubSystem hackHubSystem) {
        this.hackHubSystem = hackHubSystem;
    }

    // ======================================
    // ENDPOINT: Invia Richiesta di Supporto
    // ======================================
    @PostMapping("/invia")
    public ResponseEntity<?> inviaRichiestaSupporto(@RequestBody InviaRichiestaSupportoRequest request) {
        RichiestaSupporto richiesta = this.hackHubSystem.inviaRichiestaSupporto(
            request.messaggio(),
            request.codiceFiscaleRichiedente()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(richiesta);
    }

    // ===========================================
    // ENDPOINT: Visualizza Richieste di Supporto
    // ===========================================
    @GetMapping("/hackathon/{idHackathon}")
    public ResponseEntity<?> visualizzaRichiesteSupporto(@PathVariable Long idHackathon, @RequestParam String codiceFiscaleMentore) {
        List<RichiestaSupporto> richieste = this.hackHubSystem.visualizzaRichiesteSupporto(
            idHackathon, 
            codiceFiscaleMentore
        );

        return ResponseEntity.ok(richieste);
    }

    // ================================================
    // ENDPOINT: Visualizza Richieste Supporto Inviate
    // ================================================
    @GetMapping("/team/{idTeam}")
    public ResponseEntity<?> visualizzaRichiesteSupportoInviate(@PathVariable Long idTeam, @RequestParam String codiceFiscaleRichiedente) {
        List<RichiestaSupporto> richieste = this.hackHubSystem.visualizzaRichiesteSupportoInviate(
            idTeam, 
            codiceFiscaleRichiedente
        );

        return ResponseEntity.ok(richieste);
    }

    // =====================================
    // ENDPOINT: Prende In Carico Richiesta
    // =====================================
    @PutMapping("/prendi-in-carico/{idRichiesta}")
    public ResponseEntity<?> prendiInCaricoRichiesta(@PathVariable Long idRichiesta, @RequestBody it.unicam.cs.ids.hackhub.dto.PrendiInCaricoRequest request) {
        RichiestaSupporto richiestaAggiornata = this.hackHubSystem.prendiInCaricoRichiesta(
            idRichiesta,
            request.codiceFiscaleMentore()
        );

        return ResponseEntity.ok(richiestaAggiornata);
    }

    // =========================================
    // ENDPOINT: Risponde Richiesta di Supporto
    // =========================================
    @PutMapping("/rispondi/{idRichiesta}")
    public ResponseEntity<?> rispondiRichiestaSupporto(@PathVariable Long idRichiesta, @RequestBody it.unicam.cs.ids.hackhub.dto.RispondiRichiestaSupportoRequest request) {
        RichiestaSupporto richiestaAggiornata = this.hackHubSystem.rispondiRichiestaSupporto(
            idRichiesta,
            request.risposta(),
            request.codiceFiscaleMentore()
        );

        return ResponseEntity.ok(richiestaAggiornata);
    }
}