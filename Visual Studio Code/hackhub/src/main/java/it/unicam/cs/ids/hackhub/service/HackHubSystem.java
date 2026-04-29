package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.dto.HackathonDettagliResponse;
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
    private final SottomissioneService sottomissioneService;
    private final ValutazioneService valutazioneService;
    private final GiudiceService giudiceService;
    private final RichiestaSupportoService richiestaSupportoService;

    public HackHubSystem(UtenteService utenteService, 
                         TeamService teamService, 
                         HackathonService hackathonService,
                         InvitoService invitoService,
                         MembroDelTeamService membroDelTeamService,
                         OrganizzatoreService organizzatoreService,
                         SottomissioneService sottomissioneService,
                         ValutazioneService valutazioneService,
                         GiudiceService giudiceService,
                         RichiestaSupportoService richiestaSupportoService) {
        this.utenteService = utenteService;
        this.teamService = teamService;
        this.hackathonService = hackathonService;
        this.invitoService = invitoService;
        this.membroDelTeamService = membroDelTeamService;
        this.organizzatoreService = organizzatoreService;
        this.sottomissioneService = sottomissioneService;
        this.valutazioneService = valutazioneService;
        this.giudiceService = giudiceService;
        this.richiestaSupportoService = richiestaSupportoService;
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
                .setStato("ATTIVO")
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

        if ("DISATTIVO".equals(utente.getStato())) {
            throw new IllegalStateException("Accesso negato: Il tuo account è stato DISATTIVATO.");
        }

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

    // ==========================================
    // CASO D'USO: VISUALIZZA DETTAGLI HACKATHON
    // ==========================================
    public HackathonDettagliResponse visualizzaDettagliHackathon(Long id, String codiceFiscaleUtente) {
        Hackathon hackathon = this.hackathonService.ottieniDettagliHackathon(id)
                .orElseThrow(() -> new IllegalArgumentException("L'hackathon selezionato non è più disponibile."));

        List<String> azioniDisponibili = new ArrayList<>();

        if (codiceFiscaleUtente != null) {
            Optional<Utente> utenteOpt = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleUtente);
            if (utenteOpt.isPresent()) {
                Utente utente = utenteOpt.get();
                if (utente instanceof MembroDelTeam && "IN_ISCRIZIONE".equalsIgnoreCase(hackathon.getStato())) {
                    azioniDisponibili.add("ISCRIVI_TEAM");
                }
            }
        }

        return new HackathonDettagliResponse(hackathon, azioniDisponibili);
    }

    // =============================================
    // CASO D'USO: VISUALIZZA DETTAGLI TEAM
    // =============================================
    public Team visualizzaDettagliTeam(Long idTeam, String codiceFiscaleRichiedente) {
        Team team = this.teamService.trovaTeamPerId(idTeam)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato."));

        if (codiceFiscaleRichiedente != null) {
            MembroDelTeam membro = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscaleRichiedente)
                    .orElseThrow(() -> new IllegalStateException("Accesso negato: Utente non trovato o non sei un membro di un team."));

            if (membro.getTeam() == null || !membro.getTeam().getCodiceTeam().equals(idTeam)) {
                throw new IllegalStateException("Accesso negato: non sei autorizzato a visualizzare i dettagli di questo team.");
            }
        }

        return team;
    }
    
    // ========================================
    // CASO D'USO: VISUALIZZA ELENCO HACKATHON
    // ========================================
    public Iterable<Hackathon> visualizzaElencoHackathon() {
        return this.hackathonService.ottieniTuttiHackathon();
    }

    // ===========================
    // CASO D'USO: CREA HACKATHON
    // ===========================
    public Hackathon creaHackathon(String nome, String descrizione, String luogo, String regolamento, Integer dimensioneMassimaTeam, String premio, LocalDate dataInizio, LocalDate dataFine, String codiceFiscaleOrganizzatore) {
        Organizzatore organizzatore = this.organizzatoreService.trovaPerCodiceFiscale(codiceFiscaleOrganizzatore)
                .orElseThrow(() -> new IllegalStateException("Errore: Solo un Organizzatore può creare un Hackathon."));

        if (nome == null || nome.trim().isEmpty() || luogo == null || luogo.trim().isEmpty() || dataInizio == null || dataFine == null || regolamento == null || regolamento.trim().isEmpty() || dimensioneMassimaTeam == null || dimensioneMassimaTeam <= 0 || premio == null || premio.trim().isEmpty()) {
            throw new IllegalArgumentException("Dati non validi: nome, luogo, regolamento, dimensione massima team, premio  e date sono obbligatori.");
        }
        if (dataInizio.isAfter(dataFine)) {
            throw new IllegalArgumentException("Dati non validi: la data di inizio deve precedere la data di fine.");
        }

        Hackathon nuovoHackathon = new Hackathon();
        nuovoHackathon.setNome(nome);
        nuovoHackathon.setDescrizione(descrizione);
        nuovoHackathon.setLuogo(luogo);
        nuovoHackathon.setRegolamento(regolamento);
        nuovoHackathon.setDimensioneMassimaTeam(dimensioneMassimaTeam);
        nuovoHackathon.setPremio(premio);
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

        if (team.getNumeroMembri() > hackathon.getDimensioneMassimaTeam()) {
            throw new IllegalStateException("Errore: Team troppo grande.");
        }

        team.setHackathon(hackathon);
        return this.teamService.salvaTeam(team);
    }

    // =================================
    // CASO D'USO: CARICA SOTTOMISSIONE
    // =================================
    @Transactional
    public Sottomissione caricaSottomissione(Long idTeam, String linkProgetto, String codiceFiscaleRichiedente) {
        Team team = this.teamService.trovaTeamPerId(idTeam)
                .orElseThrow(() -> new IllegalArgumentException("Team inesistente."));
        
        MembroDelTeam richiedente = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscaleRichiedente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato o non è un membro di un team."));

        if (richiedente.getTeam() == null || !richiedente.getTeam().getCodiceTeam().equals(idTeam)) {
            throw new IllegalStateException("Permesso negato: Devi essere un membro di questo team per caricare una sottomissione.");
        }

        if (team.getHackathon() == null) {
            throw new IllegalStateException("Errore: Il team non è iscritto ad alcun Hackathon.");
        }

        if (!"IN_CORSO".equalsIgnoreCase(team.getHackathon().getStato())) {
            throw new IllegalStateException("Errore: Puoi caricare la sottomissione solo quando l'Hackathon è IN_CORSO.");
        }

        Sottomissione sottomissione = new Sottomissione();
        sottomissione.setLinkProgetto(linkProgetto);
        sottomissione.setTeam(team);

        return this.sottomissioneService.salvaSottomissione(sottomissione);
    }

    // ===================================
    // CASO D'USO: AGGIORNA SOTTOMISSIONE
    // ===================================
    @Transactional
    public Sottomissione aggiornaSottomissione(Long idSottomissione, String nuovoLink, String codiceFiscaleRichiedente) {
        Sottomissione sottomissione = this.sottomissioneService.trovaPerId(idSottomissione)
                .orElseThrow(() -> new IllegalArgumentException("Sottomissione inesistente."));

        Team team = sottomissione.getTeam();
        
        MembroDelTeam richiedente = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscaleRichiedente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato o non è un membro di un team."));

        if (richiedente.getTeam() == null || !richiedente.getTeam().getCodiceTeam().equals(team.getCodiceTeam())) {
            throw new IllegalStateException("Permesso negato: Devi essere un membro di questo team per aggiornare la sottomissione.");
        }

        if (!"IN_CORSO".equalsIgnoreCase(team.getHackathon().getStato())) {
            throw new IllegalStateException("Errore: Non puoi più modificare la sottomissione. Tempi scaduti.");
        }

        sottomissione.setLinkProgetto(nuovoLink);
        sottomissione.setDataConsegna(LocalDate.now());

        return this.sottomissioneService.salvaSottomissione(sottomissione);
    }

    // ======================================================
    // CASO D'USO: VISUALIZZA STATO E DETTAGLI SOTTOMISSIONE
    // ======================================================
    public List<Sottomissione> visualizzaSottomissioniTeam(Long idTeam, String codiceFiscaleRichiedente) {
        Team team = this.teamService.trovaTeamPerId(idTeam)
                .orElseThrow(() -> new IllegalArgumentException("Team inesistente."));
        
        MembroDelTeam richiedente = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscaleRichiedente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato o non è un membro di un team."));

        if (richiedente.getTeam() == null || !richiedente.getTeam().getCodiceTeam().equals(idTeam)) {
            throw new IllegalStateException("Permesso negato: Devi essere un membro di questo team per vederne le sottomissioni.");
        }

        return team.getSottomissioni();
    }

    // =================================================
    // CASO D'USO: VISUALIZZA SOTTOMISSIONI DA VALUTARE
    // =================================================
    public List<Sottomissione> visualizzaSottomissioniDaValutare(Long idHackathon, String codiceFiscaleGiudice) {
        this.giudiceService.trovaPerCodiceFiscale(codiceFiscaleGiudice)
                .orElseThrow(() -> new IllegalStateException("Permesso negato: Solo i Giudici possono visualizzare le sottomissioni da valutare."));

        Hackathon hackathon = this.hackathonService.ottieniDettagliHackathon(idHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente."));

        if (!"IN_VALUTAZIONE".equalsIgnoreCase(hackathon.getStato())) {
            throw new IllegalStateException("Errore: Le sottomissioni non sono ancora pronte per la valutazione.");
        }

        return this.sottomissioneService.trovaPerHackathon(idHackathon);
    }

    // =================================
    // CASO D'USO: VALUTA SOTTOMISSIONE
    // =================================
    @Transactional
    public Valutazione valutaSottomissione(Long idSottomissione, int punteggio, String commento, String codiceFiscaleGiudice) {
        Giudice giudice = this.giudiceService.trovaPerCodiceFiscale(codiceFiscaleGiudice)
                .orElseThrow(() -> new IllegalStateException("Permesso negato: Solo i Giudici possono valutare."));

        Sottomissione sottomissione = this.sottomissioneService.trovaPerId(idSottomissione)
                .orElseThrow(() -> new IllegalArgumentException("Sottomissione inesistente."));

        Hackathon hackathon = sottomissione.getTeam().getHackathon();

        if (hackathon == null || !"IN_VALUTAZIONE".equalsIgnoreCase(hackathon.getStato())) {
            throw new IllegalStateException("Errore: Non è possibile valutare in questo momento.");
        }

        if (this.valutazioneService.esisteValutazione(idSottomissione, codiceFiscaleGiudice)) {
            throw new IllegalStateException("Errore: Hai già valutato questa sottomissione.");
        }

        if (hackathon.getGiudice() == null || !hackathon.getGiudice().getCodiceFiscale().equals(codiceFiscaleGiudice)) {
            throw new IllegalStateException("Permesso negato: Non sei il Giudice assegnato a questo Hackathon.");
        }

        Valutazione valutazione = new Valutazione();
        valutazione.setPunteggio(punteggio);
        valutazione.setCommento(commento);
        valutazione.setSottomissione(sottomissione);
        valutazione.setGiudice(giudice);

        return this.valutazioneService.salvaValutazione(valutazione);
    }

    // ============================================
    // CASO D'USO: VISUALIZZA VALUTAZIONE RICEVUTA
    // ============================================
    public List<Valutazione> visualizzaValutazioneTeam(Long idTeam, String codiceFiscaleRichiedente) {
        Team team = this.teamService.trovaTeamPerId(idTeam)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato."));

        MembroDelTeam richiedente = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscaleRichiedente)
                .orElseThrow(() -> new IllegalStateException("Utente non autorizzato."));

        if (richiedente.getTeam() == null || !richiedente.getTeam().getCodiceTeam().equals(idTeam)) {
            throw new IllegalStateException("Permesso negato: Devi essere membro di questo team.");
        }

        Hackathon hackathon = team.getHackathon();
        if (hackathon == null || !"TERMINATO".equalsIgnoreCase(hackathon.getStato())) {
            throw new IllegalStateException("Le valutazioni saranno visibili al termine dell'Hackathon.");
        }

        List<Valutazione> valutazioni = new ArrayList<Valutazione>();
        for (Sottomissione sottomissione : team.getSottomissioni()) {
            valutazioni.addAll(sottomissione.getValutazioni());
        }
        
        return valutazioni;
    }

    // ============================================
    // CASO D'USO: VISUALIZZA VALUTAZIONE RICEVUTA
    // ============================================
    public List<Valutazione> visualizzaValutazioniRicevute(Long idSottomissione, String codiceFiscaleRichiedente) {
        MembroDelTeam membro = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscaleRichiedente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato o non fai parte di un team."));

        Sottomissione sottomissione = this.sottomissioneService.trovaPerId(idSottomissione)
                .orElseThrow(() -> new IllegalArgumentException("Sottomissione inesistente."));

        if (membro.getTeam() == null || !membro.getTeam().getCodiceTeam().equals(sottomissione.getTeam().getCodiceTeam())) {
            throw new IllegalStateException("Permesso negato: Puoi visualizzare solo le valutazioni del tuo team.");
        }

        Hackathon hackathon = sottomissione.getTeam().getHackathon();
        if (hackathon == null || !"TERMINATO".equalsIgnoreCase(hackathon.getStato())) {
            throw new IllegalStateException("Errore: Le valutazioni saranno visibili solo al termine dell'Hackathon (Stato TERMINATO).");
        }

        return sottomissione.getValutazioni();
    }

    // =======================================
    // CASO D'USO: GESTISCI GIUDICE HACKATHON
    // =======================================
    @Transactional
    public void nominaGiudice(Long idHackathon, String codiceFiscaleUtente, String codiceFiscaleOrganizzatore) {
        Utente richiedente = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleOrganizzatore)
                .orElseThrow(() -> new IllegalArgumentException("Organizzatore non trovato."));

        if (!(richiedente instanceof Organizzatore)) {
            throw new IllegalStateException("Permesso negato: Solo un Organizzatore può nominare un Giudice.");
        }

        Hackathon hackathon = this.hackathonService.ottieniDettagliHackathon(idHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente."));

        if (!hackathon.getOrganizzatore().getCodiceFiscale().equals(codiceFiscaleOrganizzatore)) {
            throw new IllegalStateException("Permesso negato: Puoi nominare giudici solo per i TUOI Hackathon.");
        }

        if (hackathon.getGiudice() != null) {
            String vecchioGiudiceId = hackathon.getGiudice().getCodiceFiscale();
            
            if (vecchioGiudiceId.equals(codiceFiscaleUtente)) {
                throw new IllegalStateException("Errore: Questo utente è già il Giudice assegnato a questo Hackathon.");
            }
    
            this.utenteService.retrocediAUtenteBase(vecchioGiudiceId);
        }

        if (hackathon.getGiudice() != null && hackathon.getGiudice().getCodiceFiscale().equals(codiceFiscaleUtente)) {
            throw new IllegalStateException("Errore: Questo utente è già il Giudice assegnato a questo Hackathon.");
        }

        Utente target = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleUtente)
                .orElseThrow(() -> new IllegalArgumentException("Utente da promuovere non trovato."));

        if (target instanceof Organizzatore) {
            throw new IllegalStateException("Errore: Un Organizzatore non può essere nominato Giudice.");
        }

        if (!(target instanceof Giudice)) {
            this.utenteService.promuoviAGiudice(codiceFiscaleUtente);
        }

        this.hackathonService.assegnaGiudiceAHackathon(idHackathon, codiceFiscaleUtente);
    }

    // =======================================
    // CASO D'USO: GESTISCI MENTORI HACKATHON
    // =======================================
    @Transactional
    public void nominaMentore(Long idHackathon, String codiceFiscaleNuovoMentore, String codiceFiscaleVecchioMentore, String codiceFiscaleOrganizzatore) {
        Utente richiedente = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleOrganizzatore)
                .orElseThrow(() -> new IllegalArgumentException("Organizzatore non trovato."));

        if (!(richiedente instanceof Organizzatore)) {
            throw new IllegalStateException("Permesso negato: Solo un Organizzatore può nominare un Mentore.");
        }

        Hackathon hackathon = this.hackathonService.ottieniDettagliHackathon(idHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente."));

        if (!hackathon.getOrganizzatore().getCodiceFiscale().equals(codiceFiscaleOrganizzatore)) {
            throw new IllegalStateException("Permesso negato: Puoi nominare mentori solo per i TUOI Hackathon.");
        }

        if (codiceFiscaleVecchioMentore != null && !codiceFiscaleVecchioMentore.trim().isEmpty()) {
            boolean mentoreTrovato = hackathon.getMentori().stream()
                    .anyMatch(m -> m.getCodiceFiscale().equals(codiceFiscaleVecchioMentore));
            
            if (!mentoreTrovato) {
                throw new IllegalStateException("Errore: Il vecchio mentore specificato non è assegnato a questo Hackathon.");
            }
            if (codiceFiscaleVecchioMentore.equals(codiceFiscaleNuovoMentore)) {
                throw new IllegalStateException("Errore: Stai cercando di sostituire un mentore con se stesso.");
            }

            this.hackathonService.rimuoviMentoreDaHackathon(idHackathon, codiceFiscaleVecchioMentore);
            this.utenteService.retrocediAUtenteBase(codiceFiscaleVecchioMentore);
        } else {
            if (hackathon.getMentori().size() >= 2) {
                throw new IllegalStateException("Errore: L'Hackathon ha già il numero massimo di Mentori (2). Specifica il Codice Fiscale del mentore da sostituire.");
            }
        }

        Utente target = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleNuovoMentore)
                .orElseThrow(() -> new IllegalArgumentException("Utente da promuovere non trovato."));

        if (target instanceof Organizzatore) {
            throw new IllegalStateException("Errore: Un Organizzatore non può essere nominato Mentore.");
        }

        boolean giaAssegnato = hackathon.getMentori().stream()
                .anyMatch(m -> m.getCodiceFiscale().equals(codiceFiscaleNuovoMentore));
        
        if (giaAssegnato) {
             throw new IllegalStateException("Errore: Questo utente è già Mentore per questo Hackathon.");
        }

        if (!(target instanceof Mentore)) {
            this.utenteService.promuoviAMentore(codiceFiscaleNuovoMentore);
        }

        this.hackathonService.aggiungiMentoreAHackathon(idHackathon, codiceFiscaleNuovoMentore);
    }

    // ========================================
    // CASO D'USO: INVIA RICHIESTA DI SUPPORTO
    // ========================================
    @Transactional
    public RichiestaSupporto inviaRichiestaSupporto(String messaggio, String codiceFiscaleRichiedente) {
        MembroDelTeam mittente = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscaleRichiedente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato o non sei un membro di un team."));

        if (mittente.getTeam() == null || mittente.getTeam().getHackathon() == null) {
            throw new IllegalStateException("Errore: Devi fare parte di un Team iscritto ad un Hackathon per richiedere supporto.");
        }

        Hackathon hackathon = mittente.getTeam().getHackathon();

        if (!"IN_CORSO".equalsIgnoreCase(hackathon.getStato())) {
            throw new IllegalStateException("Errore: Puoi richiedere supporto solo quando l'Hackathon è IN_CORSO.");
        }

        RichiestaSupporto richiesta = new RichiestaSupporto();
        richiesta.setMessaggio(messaggio);
        richiesta.setMittente(mittente);
        richiesta.setStato("IN_ATTESA");

        return this.richiestaSupportoService.salvaRichiesta(richiesta);
    }

    // =============================================
    // CASO D'USO: VISUALIZZA RICHIESTE DI SUPPORTO
    // =============================================
    public List<RichiestaSupporto> visualizzaRichiesteSupporto(Long idHackathon, String codiceFiscaleMentore) {
        Utente richiedente = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleMentore)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        if (!(richiedente instanceof Mentore)) {
            throw new IllegalStateException("Permesso negato: Solo i Mentori possono visualizzare le richieste di supporto.");
        }

        Hackathon hackathon = this.hackathonService.ottieniDettagliHackathon(idHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente."));

        boolean mentoreAssegnato = hackathon.getMentori().stream()
                .anyMatch(m -> m.getCodiceFiscale().equals(codiceFiscaleMentore));

        if (!mentoreAssegnato) {
            throw new IllegalStateException("Permesso negato: Non sei un Mentore assegnato a questo Hackathon.");
        }

        return this.richiestaSupportoService.trovaPerHackathon(idHackathon);
    }

    // =====================================================
    // CASO D'USO: VISUALIZZA RICHIESTE DI SUPPORTO INVIATE
    // =====================================================
    public List<RichiestaSupporto> visualizzaRichiesteSupportoInviate(Long idTeam, String codiceFiscaleRichiedente) {
        this.teamService.trovaTeamPerId(idTeam)
                .orElseThrow(() -> new IllegalArgumentException("Team inesistente."));

        MembroDelTeam richiedente = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscaleRichiedente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato o non sei membro di un team."));

        if (richiedente.getTeam() == null || !richiedente.getTeam().getCodiceTeam().equals(idTeam)) {
            throw new IllegalStateException("Permesso negato: Puoi visualizzare solo le richieste inviate dal tuo team.");
        }

        return this.richiestaSupportoService.trovaPerTeam(idTeam);
    }

    // =======================================
    // CASO D'USO: PRENDE IN CARICO RICHIESTA
    // =======================================
    @Transactional
    public RichiestaSupporto prendiInCaricoRichiesta(Long idRichiesta, String codiceFiscaleMentore) {
        Utente richiedente = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleMentore)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        if (!(richiedente instanceof Mentore mentore)) {
            throw new IllegalStateException("Permesso negato: Solo i Mentori possono prendere in carico le richieste.");
        }

        RichiestaSupporto richiesta = this.richiestaSupportoService.trovaPerId(idRichiesta)
                .orElseThrow(() -> new IllegalArgumentException("Richiesta di supporto inesistente."));

        if (!"IN_ATTESA".equals(richiesta.getStato())) {
            throw new IllegalStateException("Errore: Questa richiesta è già stata presa in carico o risolta da un altro Mentore.");
        }

        Hackathon hackathon = richiesta.getMittente().getTeam().getHackathon();
        boolean mentoreAssegnato = hackathon.getMentori().stream()
                .anyMatch(m -> m.getCodiceFiscale().equals(codiceFiscaleMentore));

        if (!mentoreAssegnato) {
            throw new IllegalStateException("Permesso negato: Non sei autorizzato a gestire le richieste di un Hackathon che non segui.");
        }

        richiesta.setMentore(mentore);
        richiesta.setStato("IN_CARICO");

        return this.richiestaSupportoService.salvaRichiesta(richiesta);
    }

    // ===========================================
    // CASO D'USO: RISPONDE RICHIESTA DI SUPPORTO
    // ===========================================
    @Transactional
    public RichiestaSupporto rispondiRichiestaSupporto(Long idRichiesta, String testoRisposta, String codiceFiscaleMentore) {
        Utente richiedente = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleMentore)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        if (!(richiedente instanceof Mentore)) {
            throw new IllegalStateException("Permesso negato: Solo i Mentori possono rispondere alle richieste.");
        }

        RichiestaSupporto richiesta = this.richiestaSupportoService.trovaPerId(idRichiesta)
                .orElseThrow(() -> new IllegalArgumentException("Richiesta di supporto inesistente."));

        if (!"IN_CARICO".equals(richiesta.getStato())) {
            throw new IllegalStateException("Errore: Puoi rispondere solo a una richiesta che è nello stato IN_CARICO.");
        }

        if (richiesta.getMentore() == null || !richiesta.getMentore().getCodiceFiscale().equals(codiceFiscaleMentore)) {
            throw new IllegalStateException("Permesso negato: Puoi rispondere solo alle richieste che hai preso in carico tu personalmente.");
        }

        richiesta.setRisposta(testoRisposta);
        richiesta.setDataRisposta(LocalDate.now());
        richiesta.setStato("RISOLTA");

        return this.richiestaSupportoService.salvaRichiesta(richiesta);
    }
}