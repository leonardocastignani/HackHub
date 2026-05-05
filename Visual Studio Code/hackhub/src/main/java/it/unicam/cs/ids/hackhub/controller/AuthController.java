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
        Utente nuovoUtente = this.hackHubSystem.registraUtente(
            request.codiceFiscale(),
            request.nome(),
            request.cognome(),
            request.email(),
            request.password()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(nuovoUtente);
    }

    // ================
    // ENDPOINT: Login
    // ================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Utente utenteLoggato = this.hackHubSystem.login(request.email(), request.password());
        
        return ResponseEntity.ok(utenteLoggato);
    }

    // =================
    // ENDPOINT: Logout
    // =================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String codiceFiscale) {
        this.hackHubSystem.logout(codiceFiscale);
        
        return ResponseEntity.ok("Sessione terminata con successo. Reindirizzamento alla Home...");
    }
}