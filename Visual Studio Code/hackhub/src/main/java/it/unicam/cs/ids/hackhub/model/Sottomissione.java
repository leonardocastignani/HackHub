package it.unicam.cs.ids.hackhub.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "sottomissioni")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sottomissione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String linkProgetto;

    @Column(nullable = false)
    private LocalDate dataConsegna = LocalDate.now();

    // Relazione: Una sottomissione appartiene a 1 Team
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonIgnoreProperties({"sottomissioni", "membri", "hackathon", "hibernateLazyInitializer", "handler"})
    private Team team;

    // Relazione: Una Sottomissione può ricevere diverse Valutazioni
    @OneToMany(mappedBy = "sottomissione", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"sottomissione", "hibernateLazyInitializer", "handler"})
    private List<Valutazione> valutazioni = new ArrayList<Valutazione>();
}