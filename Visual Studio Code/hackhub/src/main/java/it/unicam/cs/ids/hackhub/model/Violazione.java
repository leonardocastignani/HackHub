package it.unicam.cs.ids.hackhub.model;

import it.unicam.cs.ids.hackhub.model.enums.*;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "violazioni")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Violazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String motivazione;

    @Column(nullable = false)
    private LocalDate dataSegnalazione = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoViolazione statoProvvedimento = StatoViolazione.IN_ATTESA;

    @Column(length = 2000)
    private String decisione;

    @Enumerated(EnumType.STRING)
    @Column
    private EsitoViolazione esito;

    @Column
    private LocalDate dataDecisione;

    // Relazione: Segnalata da 1 Mentore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentore_id", nullable = false)
    @JsonIgnoreProperties({"violazioniSegnalate", "richiesteRicevute", "hackathonsSeguiti", "hibernateLazyInitializer", "handler"})
    private Mentore mentore;

    // Relazione: Riguarda 1 Team
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonIgnoreProperties({"violazioni", "membri", "sottomissioni", "hackathon", "hibernateLazyInitializer", "handler"})
    private Team team;
}