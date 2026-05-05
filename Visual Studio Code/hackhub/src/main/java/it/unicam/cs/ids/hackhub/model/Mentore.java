package it.unicam.cs.ids.hackhub.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@DiscriminatorValue("MENTORE")
@Data
@EqualsAndHashCode(callSuper = true)
public class Mentore extends MembroDelloStaff {
    // Relazione: Un Mentore può seguire diversi Hackathon
    @ManyToMany(mappedBy = "mentori")
    @JsonIgnoreProperties({"mentori", "teamsPartecipanti", "organizzatore", "hibernateLazyInitializer", "handler"})
    private List<Hackathon> hackathonsSeguiti = new ArrayList<Hackathon>();

    // Relazione: Un Mentore riceve molte Richieste di Supporto
    @OneToMany(mappedBy = "mentore", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"mentore", "hibernateLazyInitializer", "handler"})
    private List<RichiestaSupporto> richiesteRicevute = new ArrayList<RichiestaSupporto>();

    // Relazione: Un Mentore segnala molte Violazioni
    @OneToMany(mappedBy = "mentore", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"mentore", "hibernateLazyInitializer", "handler"})
    private List<Violazione> violazioniSegnalate = new ArrayList<Violazione>();

    // Relazione: Un Mentore propone molte Call di Mentoring
    @OneToMany(mappedBy = "mentore", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"mentore", "hibernateLazyInitializer", "handler"})
    private List<CallMentoring> callProposte = new ArrayList<CallMentoring>();
}