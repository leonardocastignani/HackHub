package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.dto.*;
import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inviti")
public class InvitoController {

    private final HackHubSystem hackHubSystem;

    public InvitoController(HackHubSystem hackHubSystem) {
        this.hackHubSystem = hackHubSystem;
    }

    // ========================
    // ENDPOINT: Invita Utente
    // ========================
    @PostMapping("/invia")
    public ResponseEntity<?> invitaUtente(@RequestBody InvitaUtenteRequest request) {
        try {
            Invito invito = this.hackHubSystem.invitaUtente(
                    request.emailUtente(), 
                    request.idTeam(), 
                    request.codiceFiscaleOwner()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(invito);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ===============================
    // ENDPOINT: Gestione Invito Team
    // ===============================
    @PostMapping("/{id}/gestisci")
    public ResponseEntity<?> gestisciInvito(@PathVariable Long id, @RequestBody GestisciInvitoRequest request) {
        try {
            Invito invitoGestito = this.hackHubSystem.gestisciInvito(
                    id, 
                    request.azione(), 
                    request.codiceFiscaleUtenteLoggato()
            );
            return ResponseEntity.ok(invitoGestito);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}