package it.unicam.cs.ids.hackhub.model;

import it.unicam.cs.ids.hackhub.model.enums.*;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "call_mentoring")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallMentoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataOra;

    @Column(length = 255)
    private String idCallEsterno;

    @Column(length = 500)
    private String linkMeet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoCall stato = StatoCall.PROGRAMMATA;

    // Relazione: Proposta da 1 Mentore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentore_id", nullable = false)
    @JsonIgnoreProperties({"callProposte", "violazioniSegnalate", "richiesteRicevute", "hackathonsSeguiti", "hibernateLazyInitializer", "handler"})
    private Mentore mentore;

    // Relazione: Destinata a 1 Team
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonIgnoreProperties({"callRicevute", "membri", "sottomissioni", "violazioni", "hackathon", "hibernateLazyInitializer", "handler"})
    private Team team;
}