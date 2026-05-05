package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.model.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public interface SottomissioneRepository extends JpaRepository<Sottomissione, Long> {
    List<Sottomissione> findByTeam_Hackathon_Id(Long idHackathon);

    @Query("SELECT COUNT(s) FROM Sottomissione s WHERE s.team.hackathon.id = :idHackathon AND s.team.stato != :statoSqualificato AND s.valutazioni IS EMPTY")
long contaSottomissioniAmmissibiliNonValutate(@Param("idHackathon") Long idHackathon, @Param("statoSqualificato") StatoTeam statoSqualificato);
}