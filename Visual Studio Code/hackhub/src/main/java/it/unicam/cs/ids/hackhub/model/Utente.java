package it.unicam.cs.ids.hackhub.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "utenti")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ruolo", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("UTENTE_BASE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utente {

    @Id
    @Column(name = "codice_fiscale", length = 16, nullable = false)
    private String codiceFiscale;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // Relazione: Un utente riceve molti inviti (0..*)
    @OneToMany(mappedBy = "destinatario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"destinatario", "hibernateLazyInitializer", "handler"})
    private List<Invito> invitiRicevuti = new ArrayList<Invito>();

    // Costruttore privato usato dal Builder
    private Utente(UtenteBuilder builder) {
        this.codiceFiscale = builder.codiceFiscale;
        this.nome = builder.nome;
        this.cognome = builder.cognome;
        this.email = builder.email;
        this.password = builder.password;
    }

    // --- IMPLEMENTAZIONE DESIGN PATTERN: BUILDER ---
    public static class UtenteBuilder {
        private String codiceFiscale;
        private String nome;
        private String cognome;
        private String email;
        private String password;

        public UtenteBuilder setCodiceFiscale(String codiceFiscale) {
            this.codiceFiscale = codiceFiscale;
            return this;
        }

        public UtenteBuilder setNome(String nome) {
            this.nome = nome;
            return this;
        }

        public UtenteBuilder setCognome(String cognome) {
            this.cognome = cognome;
            return this;
        }

        public UtenteBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public UtenteBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Utente build() {
            if (this.codiceFiscale == null || this.codiceFiscale.trim().isEmpty() ||
                this.nome == null || this.nome.trim().isEmpty() ||
                this.cognome == null || this.cognome.trim().isEmpty() ||
                this.email == null || this.email.trim().isEmpty() ||
                this.password == null || this.password.trim().isEmpty()) {
                
                throw new IllegalArgumentException("Dati non corretti: campi obbligatori mancanti.");
            }

            if (this.codiceFiscale.length() != 16) {
                throw new IllegalArgumentException("Dati non corretti: il Codice Fiscale deve essere di 16 caratteri.");
            }

            if (!this.email.contains("@")) {
                throw new IllegalArgumentException("Dati non corretti: formato email non valido.");
            }

            return new Utente(this);
        }
    }
}