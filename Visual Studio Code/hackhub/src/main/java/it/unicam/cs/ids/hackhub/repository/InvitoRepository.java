package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.model.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface InvitoRepository extends JpaRepository<Invito, Long> {
    boolean existsByDestinatario_CodiceFiscaleAndMittente_Team_CodiceTeamAndStato(String codiceFiscaleDestinatario, Long codiceTeam, StatoInvito stato);
}