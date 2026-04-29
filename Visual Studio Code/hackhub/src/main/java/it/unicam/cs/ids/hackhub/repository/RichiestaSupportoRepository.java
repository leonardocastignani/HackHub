package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public interface RichiestaSupportoRepository extends JpaRepository<RichiestaSupporto, Long> {
    List<RichiestaSupporto> findByMittente_Team_Hackathon_Id(Long idHackathon);

    List<RichiestaSupporto> findByMittente_Team_CodiceTeam(Long codiceTeam);
}