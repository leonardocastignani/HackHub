package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.dto.*;
import it.unicam.cs.ids.hackhub.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
public class GestioneStaffController {

    private final HackHubSystem hackHubSystem;

    public GestioneStaffController(HackHubSystem hackHubSystem) {
        this.hackHubSystem = hackHubSystem;
    }

    // =========================
    // ENDPOINT: Nomina Giudice
    // =========================
    @PostMapping("/nomina-giudice")
    public ResponseEntity<?> nominaGiudice(@RequestBody NominaGiudiceRequest request) {
        this.hackHubSystem.nominaGiudice(
            request.idHackathon(),
            request.codiceFiscaleUtente(),
            request.codiceFiscaleOrganizzatore()
        );

        return ResponseEntity.ok("Utente promosso a Giudice con successo.");
    }

    // =========================
    // ENDPOINT: Nomina Mentore
    // =========================
    @PostMapping("/nomina-mentore")
    public ResponseEntity<?> nominaMentore(@RequestBody NominaMentoreRequest request) {
        this.hackHubSystem.nominaMentore(
            request.idHackathon(),
            request.codiceFiscaleNuovoMentore(),
            request.codiceFiscaleVecchioMentore(), // null se non è sostituzione
            request.codiceFiscaleOrganizzatore()
        );
        
        return ResponseEntity.ok("Operazione sui Mentori completata con successo.");
    }
}