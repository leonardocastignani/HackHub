package it.unicam.cs.ids.hackhub.model;

import it.unicam.cs.ids.hackhub.model.enums.*;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "pagamenti")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double importo;

    @Column(nullable = false)
    private LocalDate dataEmissione = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoPagamento stato;

    @Column
    private String idPagamentoEsterno;

    @Column
    private String motivoErrore;

    // Relazione: Avviato da 1 Organizzatore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizzatore_id", nullable = false)
    @JsonIgnoreProperties({"hackathonOrganizzati", "hibernateLazyInitializer", "handler"})
    private Organizzatore organizzatore;

    // Relazione: Riferito a 1 Hackathon
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    @JsonIgnoreProperties({"teamsPartecipanti", "mentori", "organizzatore", "giudice", "vincitore", "hibernateLazyInitializer", "handler"})
    private Hackathon hackathon;

    // Relazione: Destinato a 1 Team Vincitore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_vincitore_id", nullable = false)
    @JsonIgnoreProperties({"membri", "sottomissioni", "violazioni", "hackathon", "hibernateLazyInitializer", "handler"})
    private Team teamVincitore;
}