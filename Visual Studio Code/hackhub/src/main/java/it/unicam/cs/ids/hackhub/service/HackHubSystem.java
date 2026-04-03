package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import java.time.*;
import java.util.*;

@Service
public class HackHubSystem {

    private final UtenteService utenteService;
    private final TeamService teamService;
    private final HackathonService hackathonService;
    private final InvitoService invitoService;
    private final MembroDelTeamService membroDelTeamService;
    private final OrganizzatoreService organizzatoreService;

    public HackHubSystem(UtenteService utenteService, 
                         TeamService teamService, 
                         HackathonService hackathonService,
                         InvitoService invitoService,
                         MembroDelTeamService membroDelTeamService,
                         OrganizzatoreService organizzatoreService) {
        this.utenteService = utenteService;
        this.teamService = teamService;
        this.hackathonService = hackathonService;
        this.invitoService = invitoService;
        this.membroDelTeamService = membroDelTeamService;
        this.organizzatoreService = organizzatoreService;
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

        if (this.utenteService.esisteEmail(email)) {
            throw new IllegalStateException("Email già registrata.");
        }
        if (this.utenteService.esisteCodiceFiscale(codiceFiscale)) {
            throw new IllegalStateException("Errore: Utente con questo Codice Fiscale già esistente.");
        }

        return this.utenteService.salvaUtente(nuovoUtente);
    }

    // ==================
    // CASO D'USO: LOGIN
    // ==================
    public Utente login(String email, String password) {
        Optional<Utente> utenteOpt = this.utenteService.trovaPerEmail(email);

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

        if (this.teamService.esisteNomeTeam(nomeTeam)) {
            throw new IllegalArgumentException("Dati non validi: il nome del team è già in uso.");
        }

        Utente owner = this.utenteService.trovaPerCodiceFiscale(ownerCodiceFiscale)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        if (owner instanceof MembroDelTeam) {
            throw new IllegalStateException("Utente Membro di un Team: non puoi creare un nuovo team.");
        }

        Team nuovoTeam = new Team();
        nuovoTeam.setNomeTeam(nomeTeam);
        nuovoTeam.setOwnerId(owner.getCodiceFiscale());
        nuovoTeam.setNumeroMembri(1);

        Team teamSalvato = this.teamService.salvaTeam(nuovoTeam);

        this.utenteService.promuoviAMembroDelTeam(owner.getCodiceFiscale(), teamSalvato.getCodiceTeam());

        return teamSalvato;
    }

    // ===========================
    // CASO D'USO: CREA HACKATHON
    // ===========================
    public Hackathon creaHackathon(String nome, String descrizione, LocalDate dataInizio, LocalDate dataFine, String codiceFiscaleOrganizzatore) {
        Organizzatore organizzatore = this.organizzatoreService.trovaPerCodiceFiscale(codiceFiscaleOrganizzatore)
                .orElseThrow(() -> new IllegalStateException("Errore: Solo un Organizzatore può creare un Hackathon."));

        if (nome == null || nome.trim().isEmpty() || dataInizio == null || dataFine == null) {
            throw new IllegalArgumentException("Dati non validi: nome e date sono obbligatori.");
        }
        if (dataInizio.isAfter(dataFine)) {
            throw new IllegalArgumentException("Dati non validi: la data di inizio deve precedere la data di fine.");
        }

        Hackathon nuovoHackathon = new Hackathon();
        nuovoHackathon.setNome(nome);
        nuovoHackathon.setDescrizione(descrizione);
        nuovoHackathon.setDataInizio(dataInizio);
        nuovoHackathon.setDataFine(dataFine);
        nuovoHackathon.setStato("IN_ISCRIZIONE");
        nuovoHackathon.setOrganizzatore(organizzatore);

        return this.hackathonService.salvaHackathon(nuovoHackathon);
    }

    // ==========================
    // CASO D'USO: INVITA UTENTE
    // ==========================
    @Transactional
    public Invito invitaUtente(String emailUtente, Long idTeam, String codiceFiscaleOwner) {
        Utente destinatario = this.utenteService.trovaPerEmail(emailUtente)
                .orElseThrow(() -> new IllegalArgumentException("Utente inesistente.")); 

        Team team = this.teamService.trovaTeamPerId(idTeam)
                .orElseThrow(() -> new IllegalArgumentException("Team inesistente."));
        
        MembroDelTeam mittente = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscaleOwner)
                .orElseThrow(() -> new IllegalArgumentException("Mittente non trovato o non è un membro del team."));

        if (!team.getOwnerId().equals(codiceFiscaleOwner)) {
            throw new IllegalStateException("Permesso negato: Solo l'Owner del Team può invitare nuovi membri.");
        }

        if (destinatario instanceof MembroDelTeam) {
            throw new IllegalStateException("Utente Membro di un Team: non può essere invitato.");
        }

        if (this.invitoService.esisteInvitoInAttesa(destinatario.getCodiceFiscale(), idTeam)) {
            throw new IllegalStateException("Invito già presente ed in attesa di risposta.");
        }

        Invito nuovoInvito = new Invito();
        nuovoInvito.setDestinatario(destinatario);
        nuovoInvito.setMittente(mittente);
        
        return this.invitoService.salvaInvito(nuovoInvito);
    }

    // =================================
    // CASO D'USO: GESTIONE INVITO TEAM
    // =================================
    @Transactional
    public Invito gestisciInvito(Long idInvito, String azione, String codiceFiscaleUtenteLoggato) {
        Invito invito = this.invitoService.trovaPerId(idInvito)
                .orElseThrow(() -> new IllegalArgumentException("Errore tecnico: Invito non trovato."));

        if (!invito.getDestinatario().getCodiceFiscale().equals(codiceFiscaleUtenteLoggato)) {
            throw new IllegalStateException("Non hai i permessi per gestire questo invito.");
        }

        if (!"IN ATTESA".equals(invito.getStato())) {
            throw new IllegalStateException("Questo invito è già stato gestito.");
        }

        if ("ACCETTA".equalsIgnoreCase(azione)) {
            invito.setStato("ACCETTATO");
            invito.setDataStato(LocalDate.now());

            Team team = invito.getMittente().getTeam();
            team.setNumeroMembri(team.getNumeroMembri() + 1);
            this.teamService.salvaTeam(team);

            this.utenteService.promuoviAMembroDelTeam(invito.getDestinatario().getCodiceFiscale(), team.getCodiceTeam());
        } else if ("RIFIUTA".equalsIgnoreCase(azione)) {
            invito.setStato("RIFIUTATO");
            invito.setDataStato(LocalDate.now());
        } else {
            throw new IllegalArgumentException("Azione non valida. Usa ACCETTA o RIFIUTA.");
        }

        return this.invitoService.salvaInvito(invito);
    }

    // ===================================
    // CASO D'USO: ISCRIVI TEAM HACKATHON
    // ===================================
    @Transactional
    public Team iscriviTeamHackathon(Long idTeam, Long idHackathon, String codiceFiscaleRichiedente) {
        Team team = this.teamService.trovaTeamPerId(idTeam)
                .orElseThrow(() -> new IllegalArgumentException("Team inesistente."));
        
        Hackathon hackathon = this.hackathonService.ottieniDettagliHackathon(idHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente."));

        if (!team.getOwnerId().equals(codiceFiscaleRichiedente)) {
            throw new IllegalStateException("Permesso negato: Solo l'Owner del Team può iscrivere il team ad un Hackathon.");
        }

        if (!"IN_ISCRIZIONE".equalsIgnoreCase(hackathon.getStato())) {
            throw new IllegalStateException("Iscrizioni chiuse per questo Hackathon.");
        }

        if (team.getHackathon() != null) {
            throw new IllegalStateException("Errore: Team già iscritto ad un Hackathon.");
        }

        if (team.getNumeroMembri() > 4) {
            throw new IllegalStateException("Errore: Team troppo grande.");
        }

        team.setHackathon(hackathon);
        return this.teamService.salvaTeam(team);
    }

    // ==========================================
    // CASO D'USO: Visualizza Dettagli Hackathon
    // ==========================================
    public Hackathon visualizzaDettagliHackathon(Long id) {
        return this.hackathonService.ottieniDettagliHackathon(id)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato."));
    }

    // =============================================
    // CASO D'USO: Visualizza Dettagli Team (Extra)
    // =============================================
    public Team visualizzaDettagliTeam(Long id) {
        return this.teamService.trovaTeamPerId(id)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato."));
    }
    
    // ==============================================
    // METODI EXTRA - Visitatore / Utente registrato
    // ==============================================
    public Iterable<Hackathon> visualizzaElencoHackathon() {
        return this.hackathonService.ottieniTuttiHackathon();
    }
}