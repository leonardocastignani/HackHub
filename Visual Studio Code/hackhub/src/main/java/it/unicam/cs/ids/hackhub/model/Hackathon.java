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

    private String nome;
    private String descrizione;
    
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String stato;
    private LocalDate scadenzaIscrizione;
    private LocalDate dataCreazione;

    // Relazione: Un Hackathon -> Molti Team
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL)
    private List<Team> teams;
}