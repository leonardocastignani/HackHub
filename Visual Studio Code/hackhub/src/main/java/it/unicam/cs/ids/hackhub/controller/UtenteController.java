package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    @PostMapping("/registrazione")
    public ResponseEntity<?> registra(@RequestBody Utente utente) {
        try {
            return ResponseEntity.ok(utenteService.registraUtente(utente));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Utente credenziali) {
        try {
            Utente u = utenteService.login(credenziali.getEmail(), credenziali.getPassword());
            return ResponseEntity.ok(u);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utente> getUtente(@PathVariable Long id) {
        return utenteService.ottieniUtente(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}