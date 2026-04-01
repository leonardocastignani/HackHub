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
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codiceTeam;

    @Column(nullable = false, unique = true)
    private String nomeTeam;

    @Column(nullable = false)
    private String ownerId; 

    @Column(nullable = false)
    private int numeroMembri = 1;

    @Column(nullable = false)
    private LocalDate dataCreazione = LocalDate.now();

    // Relazione: Un team ha molti Membri del Team (1..*)
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"team", "invitiRicevuti", "invitiGenerati", "hibernateLazyInitializer", "handler"})
    private List<MembroDelTeam> membri = new ArrayList<MembroDelTeam>();

    // Relazione: Un team partecipa a un Hackathon (0..1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id")
    @JsonIgnoreProperties({"teamsPartecipanti", "hibernateLazyInitializer", "handler"})
    private Hackathon hackathon;
}