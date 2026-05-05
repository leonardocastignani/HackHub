package it.unicam.cs.ids.hackhub.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@DiscriminatorValue("GIUDICE")
@Data
@EqualsAndHashCode(callSuper = true)
public class Giudice extends MembroDelloStaff {
    // Relazione: Un Giudice fa molte Valutazioni
    @OneToMany(mappedBy = "giudice", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"giudice", "hibernateLazyInitializer", "handler"})
    private List<Valutazione> valutazioniEffettuate = new ArrayList<Valutazione>();

    // Relazione: Un Giudice può giudicare diversi Hackathon
    @OneToMany(mappedBy = "giudice", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"giudice", "teamsPartecipanti", "organizzatore", "hibernateLazyInitializer", "handler"})
    private List<Hackathon> hackathonsGiudicati = new ArrayList<Hackathon>();
}