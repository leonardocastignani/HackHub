package it.unicam.cs.ids.hackhub.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeTeam;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private Utente owner;

    private int numeroMembri;
    private LocalDate dataCreazione;

    // Relazione: Un Team -> Molti Utenti (membri)
    @OneToMany(mappedBy = "team")
    private List<Utente> membri;

    // Relazione: Molti Team -> 1 Hackathon (partecipa)
    @ManyToOne
    @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;
}