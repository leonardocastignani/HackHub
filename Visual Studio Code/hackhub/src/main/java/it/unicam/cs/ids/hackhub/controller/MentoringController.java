package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.dto.*;
import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/mentoring")
public class MentoringController {

    private final HackHubSystem hackHubSystem;

    public MentoringController(HackHubSystem hackHubSystem) {
        this.hackHubSystem = hackHubSystem;
    }

    // =================================
    // ENDPOINT: Proponi Call Mentoring
    // =================================
    @PostMapping("/proponi-call")
    public ResponseEntity<?> proponiCall(@RequestBody ProponiCallRequest request) {
        CallMentoring callProgrammata = this.hackHubSystem.proponiCallMentoring(
            request.idTeam(),
            request.dataOra(),
            request.durataMinuti(),
            request.codiceFiscaleMentore()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(callProgrammata);
    }

    // ============================================
    // ENDPOINT: Gestisci Call - Visualizza Elenco
    // ============================================
    @GetMapping("/team/{idTeam}/calls")
    public ResponseEntity<?> visualizzaElencoCall(@PathVariable Long idTeam, @RequestParam String codiceFiscaleMembro) {
        List<CallMentoring> elencoCall = this.hackHubSystem.visualizzaElencoCall(idTeam, codiceFiscaleMembro);
        
        if (elencoCall.isEmpty()) {
            return ResponseEntity.ok("Nessuna call programmata per questo team.");
        }

        return ResponseEntity.ok(elencoCall);
    }

    // ==============================================
    // ENDPOINT: Gestisci Call - Visualizza Dettagli
    // ==============================================
    @GetMapping("/calls/{idCall}")
    public ResponseEntity<?> visualizzaDettagliCall(@PathVariable Long idCall, @RequestParam String codiceFiscaleMembro) {
        CallMentoring call = this.hackHubSystem.visualizzaDettagliCall(idCall, codiceFiscaleMembro);
        
        return ResponseEntity.ok(call);
    }

    // =======================
    // ENDPOINT: Annulla Call
    // =======================
    @PutMapping("/calls/{idCall}/annulla")
    public ResponseEntity<?> annullaCall(@PathVariable Long idCall, @RequestParam String codiceFiscaleMembro) {
        CallMentoring callAnnullata = this.hackHubSystem.annullaCall(idCall, codiceFiscaleMembro);
        
        return ResponseEntity.ok(callAnnullata);
    }
}