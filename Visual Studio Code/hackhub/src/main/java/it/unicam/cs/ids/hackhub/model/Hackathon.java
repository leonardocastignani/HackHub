package it.unicam.cs.ids.hackhub.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hackathon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 500)
    private String descrizione;

    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String stato;
    private LocalDate scadenzaIscrizione;
    private LocalDate dataCreazione = LocalDate.now();

    // Relazione: Un Hackathon ospita molti Team (0..*)
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL)
    private List<Team> teamsPartecipanti = new ArrayList<>();
}