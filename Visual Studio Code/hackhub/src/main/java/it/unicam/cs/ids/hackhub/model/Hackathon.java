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
public class Hackathon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descrizione;
    
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String stato; // Potresti usare un Enum qui (es. APERTO, CHIUSO)
    private LocalDate scadenzaIscrizione;
    private LocalDate dataCreazione;

    // Relazione: Un Hackathon -> Molti Team
    // "mappedBy" indica che la FK è nella tabella Team
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL)
    private List<Team> teams;
}