package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {
    // Esempio di query method custom (Spring la implementa automaticamente)
}