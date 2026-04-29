package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class RichiestaSupportoService {

    private final RichiestaSupportoRepository richiestaSupportoRepository;

    public RichiestaSupportoService(RichiestaSupportoRepository richiestaSupportoRepository) {
        this.richiestaSupportoRepository = richiestaSupportoRepository;
    }

    public RichiestaSupporto salvaRichiesta(RichiestaSupporto richiesta) {
        return this.richiestaSupportoRepository.save(richiesta);
    }

    public List<RichiestaSupporto> trovaPerHackathon(Long idHackathon) {
        return this.richiestaSupportoRepository.findByMittente_Team_Hackathon_Id(idHackathon);
    }

    public List<RichiestaSupporto> trovaPerTeam(Long idTeam) {
        return this.richiestaSupportoRepository.findByMittente_Team_CodiceTeam(idTeam);
    }

    public Optional<RichiestaSupporto> trovaPerId(Long id) {
        return this.richiestaSupportoRepository.findById(id);
    }
}