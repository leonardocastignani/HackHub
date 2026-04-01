package it.unicam.cs.ids.hackhub.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "inviti")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataEmissione = LocalDate.now();

    @Column(nullable = false)
    private String stato = "IN ATTESA"; 

    private LocalDate dataStato = LocalDate.now();

    // Ricevuto da 1 Utente (0..*)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_id", nullable = false)
    @JsonIgnoreProperties({"invitiRicevuti", "invitiGenerati", "team", "hibernateLazyInitializer", "handler"})
    private Utente destinatario;

    // Generato da 1 Membro del Team (0..*)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mittente_id", nullable = false)
    @JsonIgnoreProperties({"invitiGenerati", "invitiRicevuti", "team", "hibernateLazyInitializer", "handler"})
    private MembroDelTeam mittente;
}