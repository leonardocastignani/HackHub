package it.unicam.cs.ids.hackhub.model;

import com.fasterxml.jackson.annotation.*;
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

    // Relazione: Un Hackathon è organizzato da 1 Organizzatore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizzatore_id")
    @JsonIgnoreProperties({"hackathonOrganizzati", "hibernateLazyInitializer", "handler"})
    private Organizzatore organizzatore;

    // Relazione: Un Hackathon ospita molti Team (0..*)
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"hackathon", "hibernateLazyInitializer", "handler"})
    private List<Team> teamsPartecipanti = new ArrayList<Team>();
}