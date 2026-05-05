package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.model.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public interface ViolazioneRepository extends JpaRepository<Violazione, Long> {
    boolean existsByTeam_CodiceTeamAndStatoProvvedimento(Long codiceTeam, StatoViolazione statoProvvedimento);

    List<Violazione> findByTeam_Hackathon_Id(Long idHackathon);
}