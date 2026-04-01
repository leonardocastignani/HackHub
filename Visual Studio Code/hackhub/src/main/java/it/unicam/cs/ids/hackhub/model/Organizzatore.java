package it.unicam.cs.ids.hackhub.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@DiscriminatorValue("ORGANIZZATORE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Organizzatore extends MembroDelloStaff {

    // Relazione: Organizza molti Hackathon (0..*)
    @OneToMany(mappedBy = "organizzatore", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"organizzatore", "hibernateLazyInitializer", "handler"})
    private List<Hackathon> hackathonOrganizzati = new ArrayList<Hackathon>();
}