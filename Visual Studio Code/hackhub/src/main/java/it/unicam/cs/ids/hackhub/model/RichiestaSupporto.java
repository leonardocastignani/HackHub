package it.unicam.cs.ids.hackhub.model;

import it.unicam.cs.ids.hackhub.model.enums.*;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "richieste_supporto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RichiestaSupporto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String messaggio;

    @Column(nullable = false)
    private LocalDate dataRichiesta = LocalDate.now();

    @Column(length = 2000)
    private String risposta;

    @Column
    private LocalDate dataRisposta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoRichiesta stato = StatoRichiesta.IN_ATTESA;

    // Relazione: Inviata da 1 Membro del Team
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mittente_id", nullable = false)
    @JsonIgnoreProperties({"richiesteSupporto", "team", "invitiGenerati", "hibernateLazyInitializer", "handler"})
    private MembroDelTeam mittente;

    // Relazione: Ricevuta da 1 Mentore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentore_id")
    @JsonIgnoreProperties({"richiesteRicevute", "hackathonsSeguiti", "hibernateLazyInitializer", "handler"})
    private Mentore mentore;
}