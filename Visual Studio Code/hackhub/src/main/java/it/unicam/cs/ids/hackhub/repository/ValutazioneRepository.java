package it.unicam.cs.ids.hackhub.repository;

import it.unicam.cs.ids.hackhub.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface ValutazioneRepository extends JpaRepository<Valutazione, Long> {
    boolean existsBySottomissione_IdAndGiudice_CodiceFiscale(Long idSottomissione, String codiceFiscaleGiudice);
}