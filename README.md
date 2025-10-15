# HackHub: Piattaforma per la Gestione di Hackathon

[cite_start]Questo repository contiene il progetto per lo sviluppo di **HackHub**, una piattaforma web per la gestione di eventi hackathon[cite: 1, 2].

## Obiettivo del Progetto

[cite_start]L'obiettivo è realizzare una piattaforma web che supporti l'organizzazione di hackathon, la registrazione dei team e il caricamento delle loro sottomissioni[cite: 2, 5]. [cite_start]Gli hackathon sono eventi di gruppo a cui partecipano dei team[cite: 3].

## Ciclo di Vita di un Hackathon

[cite_start]Ogni hackathon sulla piattaforma segue un ciclo di vita ben definito, articolato in quattro stati[cite: 4]:
* [cite_start]**In iscrizione** [cite: 4]
* [cite_start]**In corso** [cite: 4]
* [cite_start]**In valutazione** [cite: 4]
* [cite_start]**Concluso** [cite: 4]

## Attori della Piattaforma

[cite_start]Il sistema definisce diversi ruoli (attori) con permessi e funzionalità specifiche[cite: 6].

### Visitatore
* [cite_start]È un utente non autenticato[cite: 28].
* [cite_start]Può consultare le informazioni pubbliche degli hackathon[cite: 28, 29].
* [cite_start]Deve registrarsi e accedere per utilizzare altre funzionalità[cite: 29].

### Utente
* [cite_start]È un utente registrato che gestisce la sua partecipazione tramite i team[cite: 25].
* [cite_start]Può creare un nuovo team invitando altri utenti[cite: 25].
* [cite_start]Può accettare un invito per unirsi a un team esistente[cite: 25].
* [cite_start]**Vincolo:** Può appartenere a un solo team alla volta[cite: 26].

### Membro del Team
* [cite_start]Può consultare l'elenco di tutti gli hackathon[cite: 22].
* [cite_start]Può iscrivere il proprio team a un hackathon[cite: 22].
* [cite_start]Può inviare la sottomissione del progetto entro la scadenza prevista[cite: 22].
* [cite_start]Può aggiornare la sottomissione fino alla scadenza[cite: 23].

### Membro dello Staff
* [cite_start]Categoria generica che include Organizzatore, Giudice o Mentore assegnati a un hackathon specifico[cite: 8].
* [cite_start]Può consultare l'elenco di tutti gli hackathon presenti nel sistema[cite: 9].
* [cite_start]Può accedere alle sottomissioni dei team, ma solo per gli hackathon a cui è assegnato[cite: 9].

### Organizzatore
* [cite_start]È un Membro dello Staff che crea nuovi hackathon[cite: 11].
* [cite_start]Definisce le informazioni essenziali: nome, regolamento, scadenze, date, luogo, premio, dimensione massima del team, un Giudice e uno o più Mentori[cite: 11].
* [cite_start]Può aggiungere altri Mentori anche dopo la creazione dell'hackathon[cite: 12].
* [cite_start]Proclama il team vincitore dopo la valutazione del Giudice[cite: 13].

### Mentore
* [cite_start]È un Membro dello Staff che affianca i team durante l'evento[cite: 15].
* [cite_start]Visualizza le richieste di supporto dei team[cite: 16].
* [cite_start]Può proporre una call, gestita tramite un sistema di calendario esterno[cite: 16].
* [cite_start]Può segnalare una violazione del regolamento all'Organizzatore[cite: 17].

### Giudice
* [cite_start]È un Membro dello Staff incaricato di valutare le sottomissioni al termine dell'hackathon[cite: 19].
* [cite_start]Visualizza tutte le sottomissioni dell'hackathon a cui è assegnato[cite: 20].
* [cite_start]Per ogni sottomissione, rilascia una valutazione composta da un giudizio scritto e un punteggio numerico (da 0 a 10)[cite: 20].

## Sistemi Esterni

La piattaforma si integrerà con i seguenti servizi esterni:

* [cite_start]**Calendar**: Un servizio esterno per pianificare e prenotare le call tra Mentore e team[cite: 31, 32].
* [cite_start]**Sistema di Pagamento**: Un servizio esterno per erogare il premio in denaro al team vincitore[cite: 34].

## Vincoli e Dettagli Tecnici

* [cite_start]**Sviluppo**: Il progetto deve essere sviluppato in `Java` e successivamente portato su `Spring Boot`[cite: 36].
* [cite_start]**Presentazione**: Lo strato di presentazione è a scelta dello sviluppatore, potendosi limitare a interfaccia a linea di comando e/o `API REST`[cite: 38].
* [cite_start]**Design Pattern**: È richiesto l'utilizzo di almeno due design pattern diversi dal `Singleton`[cite: 39].
