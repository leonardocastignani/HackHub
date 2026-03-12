package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {
    Optional<Utente> findByEmail(String email);
}