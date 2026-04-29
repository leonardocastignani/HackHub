package it.unicam.cs.ids.hackhub.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "valutazioni")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Valutazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int punteggio;

    @Column(length = 1000)
    private String commento;

    @Column(nullable = false)
    private LocalDate dataValutazione = LocalDate.now();

    // Relazione: Una Valutazione appartiene a 1 Sottomissione
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sottomissione_id", nullable = false)
    @JsonIgnoreProperties({"valutazioni", "team", "hibernateLazyInitializer", "handler"})
    private Sottomissione sottomissione;

    // Relazione: Una Valutazione è fatta da 1 Giudice
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giudice_id", nullable = false)
    @JsonIgnoreProperties({"valutazioniEffettuate", "hibernateLazyInitializer", "handler"})
    private Giudice giudice;
}