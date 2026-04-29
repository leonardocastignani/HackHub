package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public interface SottomissioneRepository extends JpaRepository<Sottomissione, Long> {
    List<Sottomissione> findByTeam_Hackathon_Id(Long idHackathon);
}