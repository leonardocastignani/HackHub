package it.unicam.cs.ids.hackhub.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String codiceFiscale;

    private String nome;
    private String cognome;

    @Column(unique = true)
    private String email;

    private String password;

    // Relazione: Molti Utenti -> 1 Team
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}