package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.model.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
    @Modifying
    @Query(value = "UPDATE hackathon SET giudice_id = :codiceFiscale WHERE id = :idHackathon", nativeQuery = true)
    void assegnaGiudiceAHackathon(Long idHackathon, String codiceFiscale);
    
    @Modifying
    @Query(value = "INSERT INTO hackathon_mentori (hackathon_id, mentore_id) VALUES (:idHackathon, :codiceFiscale)", nativeQuery = true)
    void aggiungiMentoreAHackathon(Long idHackathon, String codiceFiscale);

    @Modifying
    @Query(value = "DELETE FROM hackathon_mentori WHERE hackathon_id = :idHackathon AND mentore_id = :codiceFiscale", nativeQuery = true)
    void rimuoviMentoreDaHackathon(Long idHackathon, String codiceFiscale);

    List<Hackathon> findByStatoNot(StatoHackathon stato);
}