package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.service.*;
import it.unicam.cs.ids.hackhub.dto.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final HackHubSystem hackHubSystem;

    public AuthController(HackHubSystem hackHubSystem) {
        this.hackHubSystem = hackHubSystem;
    }

    // =====================================
    // ENDPOINT: Registrazione Nuovo Utente
    // =====================================
    @PostMapping("/registra")
    public ResponseEntity<?> registraUtente(@RequestBody RegistrazioneRequest request) {
        try {
            Utente nuovoUtente = hackHubSystem.registraUtente(
                request.codiceFiscale(),
                request.nome(),
                request.cognome(),
                request.email(),
                request.password()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(nuovoUtente);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore tecnico del server.");
        }
    }

    // ================
    // ENDPOINT: Login
    // ================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Utente utenteLoggato = hackHubSystem.login(request.email(), request.password());
            return ResponseEntity.ok(utenteLoggato);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}