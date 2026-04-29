package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.time.*;
import java.util.*;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, String> {
    Optional<Utente> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query(value = "UPDATE utenti SET ruolo = 'MEMBRO_TEAM', team_id = :teamId, data_ingresso = :dataIngresso WHERE codice_fiscale = :codiceFiscale", nativeQuery = true)
    void promuoviAMembroDelTeam(String codiceFiscale, Long teamId, LocalDate dataIngresso);
    
    @Modifying
    @Query(value = "UPDATE utenti SET ruolo = 'GIUDICE' WHERE codice_fiscale = :codiceFiscale", nativeQuery = true)
    void promuoviAGiudice(String codiceFiscale);
    
    @Modifying
    @Query(value = "UPDATE utenti SET ruolo = 'MENTORE' WHERE codice_fiscale = :codiceFiscale", nativeQuery = true)
    void promuoviAMentore(String codiceFiscale);
    
    @Modifying
    @Query(value = "UPDATE utenti SET ruolo = 'UTENTE_BASE' WHERE codice_fiscale = :codiceFiscale", nativeQuery = true)
    void retrocediAUtenteBase(String codiceFiscale);
}