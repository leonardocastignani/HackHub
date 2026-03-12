package it.unicam.cs.ids.hackhub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Corrisponde a 'codiceTeam' logico

    private String nomeTeam;

    // Relazione specificata nel diagramma classi (campo 'Owner')
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