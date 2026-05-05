package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.dto.*;
import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.model.enums.*;
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
    private final ViolazioneService violazioneService;
    private final PagamentoService pagamentoService;
    private final SistemaPagamentoEsterno sistemaPagamentoEsterno;
    private final CallMentoringService callMentoringService;
    private final SistemaCalendarEsterno sistemaCalendarEsterno;

    public HackHubSystem(UtenteService utenteService, 
                         TeamService teamService, 
                         HackathonService hackathonService,
                         InvitoService invitoService,
                         MembroDelTeamService membroDelTeamService,
                         OrganizzatoreService organizzatoreService,
                         SottomissioneService sottomissioneService,
                         ValutazioneService valutazioneService,
                         GiudiceService giudiceService,
                         RichiestaSupportoService richiestaSupportoService,
                         ViolazioneService violazioneService,
                         PagamentoService pagamentoService,
                         SistemaPagamentoEsterno sistemaPagamentoEsterno,
                         CallMentoringService callMentoringService,
                         SistemaCalendarEsterno sistemaCalendarEsterno) {
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
        this.violazioneService = violazioneService;
        this.pagamentoService = pagamentoService;
        this.sistemaPagamentoEsterno = sistemaPagamentoEsterno;
        this.callMentoringService = callMentoringService;
        this.sistemaCalendarEsterno = sistemaCalendarEsterno;
    }

    // =======================================
    // CASO D'USO: REGISTRAZIONE NUOVO UTENTE
    // =======================================
    @Transactional
    public Utente registraUtente(String codiceFiscale, String nome, String cognome, String email, String password) {
        Utente nuovoUtente;

        try {
            nuovoUtente = new Utente.UtenteBuilder()
                .setCodiceFiscale(codiceFiscale)
                .setNome(nome)
                .setCognome(cognome)
                .setEmail(email)
                .setPassword(password)
                .setStato(StatoUtente.ATTIVO)
                .build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
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
    @Transactional
    public Utente login(String email, String password) {
        Optional<Utente> utenteOpt = this.utenteService.trovaPerEmail(email);

        if (utenteOpt.isEmpty()) {
            throw new IllegalArgumentException("Credenziali non valide: email non trovata.");
        }

        Utente utente = utenteOpt.get();

        if (!utente.getPassword().equals(password)) {
            throw new IllegalArgumentException("Credenziali non valide: password errata.");
        }

        if (utente.getStato() == StatoUtente.DISATTIVO) {
            throw new IllegalStateException("Accesso negato: Il tuo account è stato DISATTIVATO.");
        }

        utente.setLogged(true);
        return utente;
    }

    // ============================
    // CASO D'USO: CREA NUOVO TEAM
    // ============================
    @Transactional
    public Team creaTeam(String nomeTeam, String ownerCodiceFiscale) {
        Utente owner = this.verificaLogin(ownerCodiceFiscale);

        if (nomeTeam == null || nomeTeam.trim().isEmpty()) {
            throw new IllegalArgumentException("Dati non validi: il nome del team non può essere vuoto.");
        }

        if (this.teamService.esisteNomeTeam(nomeTeam)) {
            throw new IllegalArgumentException("Dati non validi: il nome del team è già in uso.");
        }

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
    @Transactional(readOnly = true)
    public HackathonDettagliResponse visualizzaDettagliHackathon(Long id, String codiceFiscaleUtente) {
        Hackathon hackathon = this.hackathonService.ottieniDettagliHackathon(id)
                .orElseThrow(() -> new IllegalArgumentException("L'hackathon selezionato non è più disponibile."));

        List<String> azioniDisponibili = new ArrayList<String>();

        if (codiceFiscaleUtente != null) {
            Utente utente = this.verificaLogin(codiceFiscaleUtente);
            if (utente instanceof MembroDelTeam && hackathon.getStato() == StatoHackathon.IN_ISCRIZIONE) {
                azioniDisponibili.add("ISCRIVI_TEAM");
            }
        }

        return new HackathonDettagliResponse(hackathon, azioniDisponibili);
    }

    // =====================================
    // CASO D'USO: VISUALIZZA DETTAGLI TEAM
    // =====================================
    @Transactional(readOnly = true)
    public Team visualizzaDettagliTeam(Long idTeam, String codiceFiscaleRichiedente) {
        Team team = this.teamService.trovaTeamPerId(idTeam)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato."));

        if (codiceFiscaleRichiedente != null) {
            this.verificaLogin(codiceFiscaleRichiedente);
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
    @Transactional(readOnly = true)
    public Iterable<Hackathon> visualizzaElencoHackathon() {
        return this.hackathonService.ottieniTuttiHackathon();
    }

    // ===========================
    // CASO D'USO: CREA HACKATHON
    // ===========================
    public Hackathon creaHackathon(String nome, String descrizione, String luogo, String regolamento, Integer dimensioneMassimaTeam, String premio, LocalDate dataInizio, LocalDate dataFine, LocalDate scadenzaIscrizione, String codiceFiscaleOrganizzatore) {
        this.verificaLogin(codiceFiscaleOrganizzatore);
        
        Organizzatore organizzatore = this.organizzatoreService.trovaPerCodiceFiscale(codiceFiscaleOrganizzatore)
                .orElseThrow(() -> new IllegalStateException("Errore: Solo un Organizzatore può creare un Hackathon."));

        if (nome == null || nome.trim().isEmpty() || luogo == null || luogo.trim().isEmpty() || dataInizio == null || dataFine == null || scadenzaIscrizione == null || regolamento == null || regolamento.trim().isEmpty() || dimensioneMassimaTeam == null || dimensioneMassimaTeam <= 0 || premio == null || premio.trim().isEmpty()) {
            throw new IllegalArgumentException("Dati non validi: nome, luogo, regolamento, dimensione massima team, premio e date sono obbligatori.");
        }
        if (dataInizio.isAfter(dataFine)) {
            throw new IllegalArgumentException("Dati non validi: la data di inizio deve precedere la data di fine.");
        }
        if (scadenzaIscrizione.isAfter(dataInizio)) {
            throw new IllegalArgumentException("Dati non validi: la scadenza dell'iscrizione deve precedere la data di inizio.");
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
        nuovoHackathon.setScadenzaIscrizione(scadenzaIscrizione);
        nuovoHackathon.setStato(StatoHackathon.IN_ISCRIZIONE);
        nuovoHackathon.setOrganizzatore(organizzatore);

        return this.hackathonService.salvaHackathon(nuovoHackathon);
    }

    // ==========================
    // CASO D'USO: INVITA UTENTE
    // ==========================
    @Transactional
    public Invito invitaUtente(String emailUtente, Long idTeam, String codiceFiscaleOwner) {
        this.verificaLogin(codiceFiscaleOwner);

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
    public Invito gestisciInvito(Long idInvito, AzioneInvito azione, String codiceFiscaleUtenteLoggato) {
        this.verificaLogin(codiceFiscaleUtenteLoggato);
        
        Invito invito = this.invitoService.trovaPerId(idInvito)
                .orElseThrow(() -> new IllegalArgumentException("Errore tecnico: Invito non trovato."));

        if (!invito.getDestinatario().getCodiceFiscale().equals(codiceFiscaleUtenteLoggato)) {
            throw new IllegalStateException("Non hai i permessi per gestire questo invito.");
        }

        if (invito.getStato() != StatoInvito.IN_ATTESA) {
            throw new IllegalStateException("Questo invito è già stato gestito.");
        }

        switch (azione) {
            case ACCETTA -> {
                invito.setStato(StatoInvito.ACCETTATO);
                invito.setDataStato(LocalDate.now());

                Team team = invito.getMittente().getTeam();
                team.setNumeroMembri(team.getNumeroMembri() + 1);
                this.teamService.salvaTeam(team);
                
                this.utenteService.promuoviAMembroDelTeam(invito.getDestinatario().getCodiceFiscale(), team.getCodiceTeam());
            }
            case RIFIUTA -> {
                invito.setStato(StatoInvito.RIFIUTATO);
                invito.setDataStato(LocalDate.now());
            }
        }

        return this.invitoService.salvaInvito(invito);
    }

    // ===================================
    // CASO D'USO: ISCRIVI TEAM HACKATHON
    // ===================================
    @Transactional
    public Team iscriviTeamHackathon(Long idTeam, Long idHackathon, String codiceFiscaleRichiedente) {
        this.verificaLogin(codiceFiscaleRichiedente);
        
        Team team = this.teamService.trovaTeamPerId(idTeam)
                .orElseThrow(() -> new IllegalArgumentException("Team inesistente."));
        
        Hackathon hackathon = this.hackathonService.ottieniDettagliHackathon(idHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente."));

        if (!team.getOwnerId().equals(codiceFiscaleRichiedente)) {
            throw new IllegalStateException("Permesso negato: Solo l'Owner del Team può iscrivere il team ad un Hackathon.");
        }

        if (hackathon.getStato() != StatoHackathon.IN_ISCRIZIONE) {
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
        Team team = this.validazioneTeamAttivoERichiedente(idTeam, codiceFiscaleRichiedente);
        
        Hackathon hackathon = team.getHackathon();

        if (hackathon == null) {
            throw new IllegalStateException("Errore: Il team non è iscritto ad alcun Hackathon.");
        }

        if (hackathon.getStato() != StatoHackathon.IN_CORSO) {
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

        Team team = this.validazioneTeamAttivoERichiedente(sottomissione.getTeam().getCodiceTeam(), codiceFiscaleRichiedente);

        if (team.getHackathon().getStato() != StatoHackathon.IN_CORSO) {
            throw new IllegalStateException("Errore: Non puoi più modificare la sottomissione. Tempi scaduti.");
        }

        sottomissione.setLinkProgetto(nuovoLink);
        sottomissione.setDataConsegna(LocalDate.now());

        return this.sottomissioneService.salvaSottomissione(sottomissione);
    }

    // ======================================================
    // CASO D'USO: VISUALIZZA STATO E DETTAGLI SOTTOMISSIONE
    // ======================================================
    @Transactional(readOnly = true)
    public List<Sottomissione> visualizzaSottomissioniTeam(Long idTeam, String codiceFiscaleRichiedente) {
        Team team = this.validazioneTeamAttivoERichiedente(idTeam, codiceFiscaleRichiedente);

        return team.getSottomissioni();
    }

    // =================================================
    // CASO D'USO: VISUALIZZA SOTTOMISSIONI DA VALUTARE
    // =================================================
    @Transactional(readOnly = true)
    public List<Sottomissione> visualizzaSottomissioniDaValutare(Long idHackathon, String codiceFiscaleGiudice) {
        this.verificaLogin(codiceFiscaleGiudice);
        
        this.giudiceService.trovaPerCodiceFiscale(codiceFiscaleGiudice)
                .orElseThrow(() -> new IllegalStateException("Permesso negato: Solo i Giudici possono visualizzare le sottomissioni da valutare."));

        Hackathon hackathon = this.hackathonService.ottieniDettagliHackathon(idHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente."));

        if (hackathon.getStato() != StatoHackathon.IN_VALUTAZIONE) {
            throw new IllegalStateException("Errore: Le sottomissioni non sono ancora pronte per la valutazione.");
        }

        return this.sottomissioneService.trovaPerHackathon(idHackathon);
    }

    // =================================
    // CASO D'USO: VALUTA SOTTOMISSIONE
    // =================================
    @Transactional
    public Valutazione valutaSottomissione(Long idSottomissione, int punteggio, String commento, String codiceFiscaleGiudice) {
        this.verificaLogin(codiceFiscaleGiudice);
        
        Giudice giudice = this.giudiceService.trovaPerCodiceFiscale(codiceFiscaleGiudice)
                .orElseThrow(() -> new IllegalStateException("Permesso negato: Solo i Giudici possono valutare."));

        Sottomissione sottomissione = this.sottomissioneService.trovaPerId(idSottomissione)
                .orElseThrow(() -> new IllegalArgumentException("Sottomissione inesistente."));

        Hackathon hackathon = sottomissione.getTeam().getHackathon();

        if (hackathon == null || hackathon.getStato() != StatoHackathon.IN_VALUTAZIONE) {
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
    @Transactional(readOnly = true)
    public List<Valutazione> visualizzaValutazione(Long idTeam, String codiceFiscaleRichiedente) {
        Team team = validazioneTeamAttivoERichiedente(idTeam, codiceFiscaleRichiedente);

        Hackathon hackathon = team.getHackathon();
        if (hackathon == null || hackathon.getStato() != StatoHackathon.CONCLUSO) {
            throw new IllegalStateException("Le valutazioni saranno visibili al termine dell'Hackathon.");
        }

        List<Valutazione> valutazioni = new ArrayList<Valutazione>();
        for (Sottomissione sottomissione : team.getSottomissioni()) {
            valutazioni.addAll(sottomissione.getValutazioni());
        }
        
        return valutazioni;
    }

    // =======================================
    // CASO D'USO: GESTISCI GIUDICE HACKATHON
    // =======================================
    @Transactional
    public void nominaGiudice(Long idHackathon, String codiceFiscaleUtente, String codiceFiscaleOrganizzatore) {
        this.verificaLogin(codiceFiscaleOrganizzatore);
        
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
        this.verificaLogin(codiceFiscaleOrganizzatore);
        
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
        this.verificaLogin(codiceFiscaleRichiedente);
        
        MembroDelTeam mittente = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscaleRichiedente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato o non sei un membro di un team."));

        if (mittente.getTeam() == null || mittente.getTeam().getHackathon() == null) {
            throw new IllegalStateException("Errore: Devi fare parte di un Team iscritto ad un Hackathon per richiedere supporto.");
        }

        if (mittente.getTeam().getStato() == StatoTeam.SQUALIFICATO) {
            throw new IllegalStateException("Errore: Il tuo team è stato squalificato dalla competizione. Impossibile inviare richieste di supporto.");
        }

        Hackathon hackathon = mittente.getTeam().getHackathon();

        if (hackathon.getStato() != StatoHackathon.IN_CORSO) {
            throw new IllegalStateException("Errore: Puoi richiedere supporto solo quando l'Hackathon è IN_CORSO.");
        }

        RichiestaSupporto richiesta = new RichiestaSupporto();
        richiesta.setMessaggio(messaggio);
        richiesta.setMittente(mittente);
        richiesta.setStato(StatoRichiesta.IN_ATTESA);

        return this.richiestaSupportoService.salvaRichiesta(richiesta);
    }

    // =============================================
    // CASO D'USO: VISUALIZZA RICHIESTE DI SUPPORTO
    // =============================================
    @Transactional(readOnly = true)
    public List<RichiestaSupporto> visualizzaRichiesteSupporto(Long idHackathon, String codiceFiscaleMentore) {
        this.verificaLogin(codiceFiscaleMentore);
        
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
    @Transactional(readOnly = true)
    public List<RichiestaSupporto> visualizzaRichiesteSupportoInviate(Long idTeam, String codiceFiscaleRichiedente) {
        this.verificaLogin(codiceFiscaleRichiedente);
        
        this.teamService.trovaTeamPerId(idTeam)
                .orElseThrow(() -> new IllegalArgumentException("Team inesistente."));

        MembroDelTeam richiedente = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscaleRichiedente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato o non sei membro di un team."));

        if (richiedente.getTeam() == null || !richiedente.getTeam().getCodiceTeam().equals(idTeam)) {
            throw new IllegalStateException("Permesso negato: Puoi visualizzare solo le richieste inviate dal tuo team.");
        }

        if (richiedente.getTeam().getStato() == StatoTeam.SQUALIFICATO) {
            throw new IllegalStateException("Errore: Il tuo team è stato squalificato dalla competizione. Impossibile visualizzare richieste di supporto.");
        }

        return this.richiestaSupportoService.trovaPerTeam(idTeam);
    }

    // =======================================
    // CASO D'USO: PRENDE IN CARICO RICHIESTA
    // =======================================
    @Transactional
    public RichiestaSupporto prendiInCaricoRichiesta(Long idRichiesta, String codiceFiscaleMentore) {
        this.verificaLogin(codiceFiscaleMentore);
        
        Utente richiedente = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleMentore)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        if (!(richiedente instanceof Mentore mentore)) {
            throw new IllegalStateException("Permesso negato: Solo i Mentori possono prendere in carico le richieste.");
        }

        RichiestaSupporto richiesta = this.richiestaSupportoService.trovaPerId(idRichiesta)
                .orElseThrow(() -> new IllegalArgumentException("Richiesta di supporto inesistente."));

        if (richiesta.getStato() != StatoRichiesta.IN_ATTESA) {
            throw new IllegalStateException("Errore: Questa richiesta è già stata presa in carico o risolta da un altro Mentore.");
        }

        Hackathon hackathon = richiesta.getMittente().getTeam().getHackathon();
        boolean mentoreAssegnato = hackathon.getMentori().stream()
                .anyMatch(m -> m.getCodiceFiscale().equals(codiceFiscaleMentore));

        if (!mentoreAssegnato) {
            throw new IllegalStateException("Permesso negato: Non sei autorizzato a gestire le richieste di un Hackathon che non segui.");
        }

        richiesta.setMentore(mentore);
        richiesta.setStato(StatoRichiesta.IN_CARICO);

        return this.richiestaSupportoService.salvaRichiesta(richiesta);
    }

    // ===========================================
    // CASO D'USO: RISPONDE RICHIESTA DI SUPPORTO
    // ===========================================
    @Transactional
    public RichiestaSupporto rispondiRichiestaSupporto(Long idRichiesta, String testoRisposta, String codiceFiscaleMentore) {
        this.verificaLogin(codiceFiscaleMentore);

        Utente richiedente = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleMentore)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        if (!(richiedente instanceof Mentore)) {
            throw new IllegalStateException("Permesso negato: Solo i Mentori possono rispondere alle richieste.");
        }

        RichiestaSupporto richiesta = this.richiestaSupportoService.trovaPerId(idRichiesta)
                .orElseThrow(() -> new IllegalArgumentException("Richiesta di supporto inesistente."));

        if (richiesta.getStato() != StatoRichiesta.IN_CARICO) {
            throw new IllegalStateException("Errore: Puoi rispondere solo a una richiesta che è nello stato IN_CARICO.");
        }

        if (richiesta.getMentore() == null || !richiesta.getMentore().getCodiceFiscale().equals(codiceFiscaleMentore)) {
            throw new IllegalStateException("Permesso negato: Puoi rispondere solo alle richieste che hai preso in carico tu personalmente.");
        }

        richiesta.setRisposta(testoRisposta);
        richiesta.setDataRisposta(LocalDate.now());
        richiesta.setStato(StatoRichiesta.RISOLTA);

        return this.richiestaSupportoService.salvaRichiesta(richiesta);
    }

    // ===============================
    // CASO D'USO: SEGNALA VIOLAZIONE
    // ===============================
    @Transactional
    public Violazione segnalaViolazione(Long idTeam, String motivazione, String codiceFiscaleMentore) {
        this.verificaLogin(codiceFiscaleMentore);
        
        Utente richiedente = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleMentore)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        if (!(richiedente instanceof Mentore mentore)) {
            throw new IllegalStateException("Permesso negato: Solo i Mentori possono segnalare violazioni.");
        }

        Team teamTarget = this.teamService.trovaTeamPerId(idTeam)
                .orElseThrow(() -> new IllegalArgumentException("Team inesistente."));

        Hackathon hackathon = teamTarget.getHackathon();
        if (hackathon == null) {
            throw new IllegalStateException("Errore: Il Team selezionato non partecipa ad alcun Hackathon.");
        }

        boolean mentoreAssegnato = hackathon.getMentori().stream()
                .anyMatch(m -> m.getCodiceFiscale().equals(codiceFiscaleMentore));

        if (!mentoreAssegnato) {
            throw new IllegalStateException("Permesso negato: Puoi segnalare solo i Team che partecipano agli Hackathon che supervisioni.");
        }

        if (this.violazioneService.esisteViolazioneInAttesaPerTeam(idTeam)) {
            throw new IllegalStateException("Attenzione: Esiste già una segnalazione in attesa per questo Team. Attendi che l'Organizzatore la gestisca.");
        }

        Violazione violazione = new Violazione();
        violazione.setMotivazione(motivazione);
        violazione.setTeam(teamTarget);
        violazione.setMentore(mentore);
        violazione.setStatoProvvedimento(StatoViolazione.IN_ATTESA);

        return this.violazioneService.salvaViolazione(violazione);
    }

    // ==================================================
    // CASO D'USO: GESTISCI VIOLAZIONE / SQUALIFICA TEAM
    // ==================================================
    @Transactional
    public Violazione gestisciViolazione(Long idViolazione, String decisione, EsitoViolazione esito, String codiceFiscaleOrganizzatore) {
        this.verificaLogin(codiceFiscaleOrganizzatore);
        
        Utente richiedente = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleOrganizzatore)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        if (!(richiedente instanceof Organizzatore)) {
            throw new IllegalStateException("Permesso negato: Solo gli Organizzatori possono gestire le violazioni.");
        }

        Violazione violazione = this.violazioneService.trovaPerId(idViolazione)
                .orElseThrow(() -> new IllegalArgumentException("Violazione inesistente."));

        if (violazione.getStatoProvvedimento() == StatoViolazione.RISOLTA) {
            throw new IllegalStateException("Errore: Questa segnalazione è già stata gestita e risolta.");
        }

        Hackathon hackathon = violazione.getTeam().getHackathon();
        if (!hackathon.getOrganizzatore().getCodiceFiscale().equals(codiceFiscaleOrganizzatore)) {
            throw new IllegalStateException("Permesso negato: Non puoi gestire violazioni di Hackathon che non hai organizzato.");
        }

        violazione.setDecisione(decisione);
        violazione.setEsito(esito);
        violazione.setDataDecisione(LocalDate.now());
        violazione.setStatoProvvedimento(StatoViolazione.RISOLTA);

        if (esito == EsitoViolazione.SQUALIFICA) {
            Team teamTarget = violazione.getTeam();

            if (teamTarget.getStato() == StatoTeam.SQUALIFICATO) {
                this.violazioneService.salvaViolazione(violazione);
                throw new IllegalStateException("Il Team è già stato squalificato da questo Hackathon. La segnalazione è stata comunque marcata come RISOLTA.");
            } else {
                this.teamService.squalificaTeam(teamTarget);
            }
        }

        return this.violazioneService.salvaViolazione(violazione);
    }

    // ====================================
    // CASO D'USO: PROCLAMA TEAM VINCITORE
    // ====================================
    @Transactional
    public Hackathon proclamaVincitore(Long idHackathon, Long idTeamVincitore, String codiceFiscaleOrganizzatore) {
        this.verificaLogin(codiceFiscaleOrganizzatore);
        
        Utente richiedente = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleOrganizzatore)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        if (!(richiedente instanceof Organizzatore)) {
            throw new IllegalStateException("Permesso negato: Solo gli Organizzatori possono proclamare il vincitore.");
        }

        Hackathon hackathon = this.hackathonService.ottieniDettagliHackathon(idHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente."));

        if (!hackathon.getOrganizzatore().getCodiceFiscale().equals(codiceFiscaleOrganizzatore)) {
            throw new IllegalStateException("Permesso negato: Puoi gestire solo i TUOI Hackathon.");
        }

        if (hackathon.getStato() != StatoHackathon.IN_VALUTAZIONE) {
            throw new IllegalStateException("Errore: È possibile proclamare il vincitore solo quando l'Hackathon è IN_VALUTAZIONE.");
        }

        long nonValutate = this.sottomissioneService.contaSottomissioniAmmissibiliNonValutate(idHackathon, StatoTeam.SQUALIFICATO);
        if (nonValutate > 0) {
            throw new IllegalStateException("Operazione bloccata: Ci sono sottomissioni ammissibili non ancora valutate dal Giudice.");
        }

        Team teamVincitore = this.teamService.trovaTeamPerId(idTeamVincitore)
                .orElseThrow(() -> new IllegalArgumentException("Team vincitore inesistente."));

        if (teamVincitore.getHackathon() == null || !teamVincitore.getHackathon().getId().equals(idHackathon)) {
            throw new IllegalStateException("Errore: Il Team selezionato non partecipa a questo Hackathon.");
        }

        if (teamVincitore.getStato() == StatoTeam.SQUALIFICATO) {
            throw new IllegalStateException("Errore critico: Non è possibile proclamare vincitore un Team squalificato!");
        }

        hackathon.setVincitore(teamVincitore);
        hackathon.setStato(StatoHackathon.CONCLUSO);

        return this.hackathonService.salvaHackathon(hackathon);
    }

    // ===================================
    // CASO D'USO: AVVIA PAGAMENTO PREMIO
    // ===================================
    @Transactional
    public Pagamento avviaPagamentoPremio(Long idHackathon, String codiceFiscaleOrganizzatore) {
        this.verificaLogin(codiceFiscaleOrganizzatore);
        
        Utente richiedente = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleOrganizzatore)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        if (!(richiedente instanceof Organizzatore organizzatore)) {
            throw new IllegalStateException("Permesso negato: Solo gli Organizzatori possono avviare pagamenti.");
        }

        Hackathon hackathon = this.hackathonService.ottieniDettagliHackathon(idHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente."));

        if (!hackathon.getOrganizzatore().getCodiceFiscale().equals(codiceFiscaleOrganizzatore)) {
            throw new IllegalStateException("Permesso negato: Puoi erogare premi solo per i TUOI Hackathon.");
        }

        if (hackathon.getStato() != StatoHackathon.CONCLUSO) {
            throw new IllegalStateException("Errore: Puoi erogare il premio solo quando l'Hackathon è in stato CONCLUSO.");
        }

        Team vincitore = hackathon.getVincitore();
        if (vincitore == null) {
            throw new IllegalStateException("Errore critico: Nessun vincitore registrato per questo Hackathon.");
        }

        double importo;
        try {
            String numerico = hackathon.getPremio().replaceAll("[^\\d.,]", "").replace(",", ".");
            importo = Double.parseDouble(numerico);
        } catch (Exception e) {
            throw new IllegalStateException("Impossibile determinare l'importo numerico dal premio specificato: " + hackathon.getPremio());
        }

        if (importo <= 0) {
            throw new IllegalStateException("Errore: Il premio deve essere maggiore di 0 per avviare il pagamento.");
        }

        // --- CONTATTO CON IL SISTEMA DI PAGAMENTO ESTERNO ---
        SistemaPagamentoEsterno.EsitoTransazione esito = this.sistemaPagamentoEsterno.processaPagamento(importo, "IBAN-" + vincitore.getCodiceTeam());

        // --- CREAZIONE RECORD PAGAMENTO ---
        Pagamento pagamento = new Pagamento();
        pagamento.setImporto(importo);
        pagamento.setOrganizzatore(organizzatore);
        pagamento.setHackathon(hackathon);
        pagamento.setTeamVincitore(vincitore);

        if (esito.successo()) {
            pagamento.setStato(StatoPagamento.COMPLETATO);
            pagamento.setIdPagamentoEsterno(esito.idTransazione());
        } else {
            pagamento.setStato(StatoPagamento.FALLITO);
            pagamento.setMotivoErrore(esito.errore());
        }

        return this.pagamentoService.salvaPagamento(pagamento);
    }

    // ===========================================================
    // CASO D'USO: PROPONI CALL MENTORING / PRENOTA SLOT CALENDAR
    // ===========================================================
    @Transactional
    public CallMentoring proponiCallMentoring(Long idTeam, LocalDateTime dataOra, int durataMinuti, String codiceFiscaleMentore) {
        this.verificaLogin(codiceFiscaleMentore);
        
        Utente richiedente = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleMentore)
                .orElseThrow(() -> new IllegalArgumentException("Utente inesistente."));

        if (!(richiedente instanceof Mentore mentore)) {
            throw new IllegalStateException("Permesso negato: Solo i Mentori possono proporre call.");
        }

        Team teamDestinatario = this.teamService.trovaTeamPerId(idTeam)
                .orElseThrow(() -> new IllegalArgumentException("Team inesistente."));

        Hackathon hackathon = teamDestinatario.getHackathon();
        
        if (hackathon == null || hackathon.getStato() != StatoHackathon.IN_CORSO) {
            throw new IllegalStateException("Errore: È possibile proporre call solo a team iscritti ad Hackathon IN_CORSO.");
        }

        if (teamDestinatario.getStato() == StatoTeam.SQUALIFICATO) {
            throw new IllegalStateException("Errore: Il Team è stato squalificato, impossibile proporre call di mentoring.");
        }

        boolean mentoreAssegnato = hackathon.getMentori().stream()
                .anyMatch(m -> m.getCodiceFiscale().equals(codiceFiscaleMentore));

        if (!mentoreAssegnato) {
            throw new IllegalStateException("Permesso negato: Puoi proporre call solo ai Team degli Hackathon che supervisioni.");
        }

        // --- CONTATTO CON IL SISTEMA CALENDAR ESTERNO ---
        SistemaCalendarEsterno.RispostaCalendar rispostaApi = this.sistemaCalendarEsterno.prenotaSlot(dataOra, durataMinuti);

        if (!rispostaApi.successo()) {
            throw new IllegalStateException("Prenotazione fallita: " + rispostaApi.errore());
        }

        CallMentoring nuovaCall = new CallMentoring();
        nuovaCall.setDataOra(dataOra);
        nuovaCall.setIdCallEsterno(rispostaApi.idCallEsterno());
        nuovaCall.setLinkMeet(rispostaApi.linkMeet());
        nuovaCall.setMentore(mentore);
        nuovaCall.setTeam(teamDestinatario);
        nuovaCall.setStato(StatoCall.PROGRAMMATA);

        return this.callMentoringService.salvaCall(nuovaCall);
    }

    // ===================================================
    // CASO D'USO: GESTISCI CALL - Visualizza Elenco Call
    // ===================================================
    @Transactional(readOnly = true)
    public List<CallMentoring> visualizzaElencoCall(Long idTeam, String codiceFiscaleMembro) {
        this.verificaLogin(codiceFiscaleMembro);
        
        MembroDelTeam membro = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscaleMembro)
                .orElseThrow(() -> new IllegalArgumentException("Membro inesistente."));

        if (membro.getTeam() == null || !membro.getTeam().getCodiceTeam().equals(idTeam)) {
            throw new IllegalStateException("Permesso negato: Non fai parte di questo Team.");
        }

        if (membro.getTeam().getStato() == StatoTeam.SQUALIFICATO) {
            throw new IllegalStateException("Errore: Il Team è stato squalificato, impossibile visualizzare le call di mentoring.");
        }

        return this.callMentoringService.visualizzaElencoCallTeam(idTeam);
    }

    // =====================================================
    // CASO D'USO: GESTISCI CALL - Visualizza Dettagli Call
    // =====================================================
    @Transactional(readOnly = true)
    public CallMentoring visualizzaDettagliCall(Long idCall, String codiceFiscaleMembro) {
        this.verificaLogin(codiceFiscaleMembro);
        
        MembroDelTeam membro = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscaleMembro)
                .orElseThrow(() -> new IllegalArgumentException("Membro inesistente."));

        CallMentoring call = this.callMentoringService.trovaPerId(idCall)
                .orElseThrow(() -> new IllegalArgumentException("Call inesistente."));

        if (membro.getTeam() == null || !membro.getTeam().getCodiceTeam().equals(call.getTeam().getCodiceTeam())) {
            throw new IllegalStateException("Permesso negato: Non sei autorizzato a visualizzare i dettagli di questa call.");
        }

        if (membro.getTeam().getStato() == StatoTeam.SQUALIFICATO) {
            throw new IllegalStateException("Errore: Il Team è stato squalificato, impossibile visualizzare i dettagli di questa call.");
        }

        return call;
    }

    // =========================
    // CASO D'USO: ANNULLA CALL
    // =========================
    @Transactional
    public CallMentoring annullaCall(Long idCall, String codiceFiscaleMembro) {
        this.verificaLogin(codiceFiscaleMembro);
        
        MembroDelTeam membro = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscaleMembro)
                .orElseThrow(() -> new IllegalArgumentException("Membro inesistente."));

        CallMentoring call = this.callMentoringService.trovaPerId(idCall)
                .orElseThrow(() -> new IllegalArgumentException("Call inesistente."));

        if (membro.getTeam() == null || !membro.getTeam().getCodiceTeam().equals(call.getTeam().getCodiceTeam())) {
            throw new IllegalStateException("Permesso negato: Non puoi annullare una call che non appartiene al tuo Team.");
        }

        if (membro.getTeam().getStato() == StatoTeam.SQUALIFICATO) {
            throw new IllegalStateException("Errore: Il Team è stato squalificato, impossibile annullare questa call.");
        }

        if (call.getStato() != StatoCall.PROGRAMMATA) {
            throw new IllegalStateException("Errore: Impossibile annullare una call in stato " + call.getStato() + ".");
        }

        // --- CONTATTO CON IL SISTEMA CALENDAR ESTERNO ---
        boolean successoEsterno = false;
        if (call.getIdCallEsterno() != null) {
            successoEsterno = this.sistemaCalendarEsterno.annullaSlot(call.getIdCallEsterno());
        }

        // --- AGGIORNAMENTO STATO LOCALE ---
        call.setStato(StatoCall.ANNULLATA);
        CallMentoring callSalvata = this.callMentoringService.salvaCall(call);

        if (!successoEsterno && call.getIdCallEsterno() != null) {
            throw new IllegalStateException("La Call è stata ANNULLATA nel sistema, ma non è stato possibile sincronizzare l'annullamento con il Calendar esterno.");
        }

        return callSalvata;
    }

    // ===================
    // CASO D'USO: LOGOUT
    // ===================
    @Transactional
    public void logout(String codiceFiscale) {
        this.verificaLogin(codiceFiscale);

        Utente utente = this.utenteService.trovaPerCodiceFiscale(codiceFiscale)
                .orElseThrow(() -> new IllegalArgumentException("Sessione non valida o utente inesistente."));

        if (!utente.isLogged()) {
             throw new IllegalStateException("L'utente non è attualmente loggato.");
        }

        utente.setLogged(false);
        this.utenteService.salvaUtente(utente);
    }

    // =====================================
    // CASO D'USO: AGGIORNA STATO HACKATHON
    // =====================================
    @Transactional
    public Hackathon aggiornaStatoHackathon(Long idHackathon, String nuovoStato, String codiceFiscaleOrganizzatore) {
        this.verificaLogin(codiceFiscaleOrganizzatore);
        
        Utente richiedente = this.utenteService.trovaPerCodiceFiscale(codiceFiscaleOrganizzatore)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        if (!(richiedente instanceof Organizzatore)) {
            throw new IllegalStateException("Permesso negato: Solo gli Organizzatori possono forzare lo stato di un Hackathon.");
        }

        Hackathon hackathon = this.hackathonService.ottieniDettagliHackathon(idHackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon inesistente."));

        if (!hackathon.getOrganizzatore().getCodiceFiscale().equals(codiceFiscaleOrganizzatore)) {
            throw new IllegalStateException("Permesso negato: Puoi modificare solo lo stato dei TUOI Hackathon.");
        }

        StatoHackathon statoEnum;
        try {
            statoEnum = StatoHackathon.valueOf(nuovoStato.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Stato non valido. Usa uno tra: " + Arrays.toString(StatoHackathon.values()));
        }

        hackathon.setStato(statoEnum);
        return this.hackathonService.salvaHackathon(hackathon);
    }

    // ================================
    // HELPER: VERIFICA STATO DI LOGIN
    // ================================
    private Utente verificaLogin(String codiceFiscale) {
        Utente utente = this.utenteService.trovaPerCodiceFiscale(codiceFiscale)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        if (!utente.isLogged()) {
            throw new IllegalStateException("Accesso negato: devi prima effettuare il login.");
        }

        return utente;
    }

    // ===================================================
    // HELPER: VALIDAZIONE APPARTENENZA TEAM E SQUALIFICA
    // ===================================================
    private Team validazioneTeamAttivoERichiedente(Long idTeam, String codiceFiscale) {
        this.verificaLogin(codiceFiscale);

        Team team = this.teamService.trovaTeamPerId(idTeam)
                .orElseThrow(() -> new IllegalArgumentException("Team inesistente."));

        if (team.getStato() == StatoTeam.SQUALIFICATO) {
            throw new IllegalStateException("Errore: Il tuo team è stato squalificato dalla competizione. Operazione non consentita.");
        }

        MembroDelTeam richiedente = this.membroDelTeamService.trovaPerCodiceFiscale(codiceFiscale)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato o non sei un membro di un team."));

        if (richiedente.getTeam() == null || !richiedente.getTeam().getCodiceTeam().equals(idTeam)) {
            throw new IllegalStateException("Permesso negato: Devi essere un membro di questo team per eseguire l'operazione.");
        }

        return team;
    }
}