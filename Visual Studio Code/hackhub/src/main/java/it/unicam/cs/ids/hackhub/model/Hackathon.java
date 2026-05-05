package it.unicam.cs.ids.hackhub.model;

import it.unicam.cs.ids.hackhub.model.enums.*;
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

    @Column(nullable = false)
    private String luogo;

    @Column(length = 2000)
    private String regolamento;

    private Integer dimensioneMassimaTeam;

    private String premio;

    private LocalDate dataInizio;
    private LocalDate dataFine;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoHackathon stato;
    
    private LocalDate scadenzaIscrizione;
    private LocalDate dataCreazione = LocalDate.now();

    // Relazione: Un Hackathon è organizzato da 1 Organizzatore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizzatore_id")
    @JsonIgnoreProperties({"hackathonOrganizzati", "hibernateLazyInitializer", "handler"})
    private Organizzatore organizzatore;

    // Relazione: Un Hackathon ha 1 Giudice
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giudice_id")
    @JsonIgnoreProperties({"hackathonsGiudicati", "valutazioniEffettuate", "hibernateLazyInitializer", "handler"})
    private Giudice giudice;

    // Relazione: Un Hackathon ha fino a 2 Mentori
    @ManyToMany
    @JoinTable(
        name = "hackathon_mentori",
        joinColumns = @JoinColumn(name = "hackathon_id"),
        inverseJoinColumns = @JoinColumn(name = "mentore_id")
    )
    @JsonIgnoreProperties({"hackathonsSeguiti", "hibernateLazyInitializer", "handler"})
    private List<Mentore> mentori = new ArrayList<Mentore>();

    // Relazione: Un Hackathon ospita molti Team (0..*)
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"hackathon", "hibernateLazyInitializer", "handler"})
    private List<Team> teamsPartecipanti = new ArrayList<Team>();

    // Relazione: Un Hackathon ha 1 Team vincitore (0..1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vincitore_id")
    @JsonIgnoreProperties({"hackathon", "membri", "sottomissioni", "violazioni", "hibernateLazyInitializer", "handler"})
    private Team vincitore;
}