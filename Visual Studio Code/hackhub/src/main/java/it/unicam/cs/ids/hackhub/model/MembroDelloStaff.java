package it.unicam.cs.ids.hackhub.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("STAFF")
@Data
@EqualsAndHashCode(callSuper = true)
public class MembroDelloStaff extends Utente {
}