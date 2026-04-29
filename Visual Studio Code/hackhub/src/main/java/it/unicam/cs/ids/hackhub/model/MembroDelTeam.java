package it.unicam.cs.ids.hackhub.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Entity
@DiscriminatorValue("MEMBRO_TEAM")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MembroDelTeam extends Utente {

    private LocalDate dataIngresso = LocalDate.now();

    // Relazione: Appartiene a 1 Team (0..1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @JsonIgnoreProperties({"membri", "hibernateLazyInitializer", "handler"})
    private Team team;

    // Relazione: Genera molti Inviti (0..*)
    @OneToMany(mappedBy = "mittente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"mittente", "hibernateLazyInitializer", "handler"})
    private List<Invito> invitiGenerati = new ArrayList<Invito>();

    // Relazione: Un Membro invia molte Richieste di Supporto
    @OneToMany(mappedBy = "mittente", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"mittente", "hibernateLazyInitializer", "handler"})
    private List<RichiestaSupporto> richiesteSupporto = new ArrayList<>();
}