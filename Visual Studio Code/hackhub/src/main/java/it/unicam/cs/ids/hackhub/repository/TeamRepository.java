package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByNomeTeam(String nomeTeam);
}