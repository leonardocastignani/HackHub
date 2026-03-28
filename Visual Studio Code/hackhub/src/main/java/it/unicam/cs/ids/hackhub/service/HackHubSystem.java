package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import java.util.*;

@Service
public class HackHubSystem {

    private final UtenteService utenteService;
    private final TeamService teamService;
    private final HackathonService hackathonService;

    public HackHubSystem(UtenteService utenteService, 
                         TeamService teamService, 
                         HackathonService hackathonService) {
        this.utenteService = utenteService;
        this.teamService = teamService;
        this.hackathonService = hackathonService;
    }

    // =======================================
    // CASO D'USO: REGISTRAZIONE NUOVO UTENTE
    // =======================================
    public Utente registraUtente(String codiceFiscale, String nome, String cognome, String email, String password) {
        Utente nuovoUtente;

        try {
            nuovoUtente = new Utente.UtenteBuilder()
                .setCodiceFiscale(codiceFiscale)
                .setNome(nome)
                .setCognome(cognome)
                .setEmail(email)
                .setPassword(password)
                .build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Dati non corretti: " + e.getMessage());
        }

        if (utenteService.esisteEmail(email)) {
            throw new IllegalStateException("Email già registrata.");
        }
        if (utenteService.esisteCodiceFiscale(codiceFiscale)) {
            throw new IllegalStateException("Errore: Utente con questo Codice Fiscale già esistente.");
        }

        return utenteService.salvaUtente(nuovoUtente);
    }

    // ==================
    // CASO D'USO: LOGIN
    // ==================
    public Utente login(String email, String password) {
        Optional<Utente> utenteOpt = utenteService.trovaPerEmail(email);

        if (utenteOpt.isEmpty()) {
            throw new IllegalArgumentException("Credenziali non valide: email non trovata.");
        }

        Utente utente = utenteOpt.get();

        if (!utente.getPassword().equals(password)) {
            throw new IllegalArgumentException("Credenziali non valide: password errata.");
        }

        // NB: Non abbiamo un campo "stato", implementarlo!!!!

        return utente;
    }

    // ============================
    // CASO D'USO: CREA NUOVO TEAM
    // ============================
    @Transactional
    public Team creaTeam(String nomeTeam, String ownerCodiceFiscale) {
        if (nomeTeam == null || nomeTeam.trim().isEmpty()) {
            throw new IllegalArgumentException("Dati non validi: il nome del team non può essere vuoto.");
        }

        if (teamService.esisteNomeTeam(nomeTeam)) {
            throw new IllegalArgumentException("Dati non validi: il nome del team è già in uso.");
        }

        Utente owner = utenteService.trovaPerCodiceFiscale(ownerCodiceFiscale)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        if (owner.getTeam() != null) {
            throw new IllegalStateException("Utente Membro di un Team: non puoi creare un nuovo team.");
        }

        Team nuovoTeam = new Team();
        nuovoTeam.setNomeTeam(nomeTeam);
        nuovoTeam.setOwnerId(owner.getCodiceFiscale());
        nuovoTeam.setNumeroMembri(1);

        Team teamSalvato = teamService.salvaTeam(nuovoTeam);

        owner.setTeam(teamSalvato);
        utenteService.salvaUtente(owner);

        return teamSalvato;
    }

    // ==========================================
    // CASO D'USO: Visualizza Dettagli Hackathon
    // ==========================================
    public Hackathon visualizzaDettagliHackathon(Long id) {
        return hackathonService.ottieniDettagliHackathon(id)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato."));
    }

    // =============================================
    // CASO D'USO: Visualizza Dettagli Team (Extra)
    // =============================================
    public Team visualizzaDettagliTeam(Long id) {
        return teamService.trovaTeamPerId(id)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato."));
    }
    
    // ==============================================
    // METODI EXTRA - Visitatore / Utente registrato
    // ==============================================
    public Iterable<Hackathon> visualizzaElencoHackathon() {
        return hackathonService.ottieniTuttiHackathon();
    }
}