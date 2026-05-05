package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.model.enums.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class ViolazioneService {

    private final ViolazioneRepository violazioneRepository;

    public ViolazioneService(ViolazioneRepository violazioneRepository) {
        this.violazioneRepository = violazioneRepository;
    }

    public Violazione salvaViolazione(Violazione violazione) {
        return this.violazioneRepository.save(violazione);
    }

    public boolean esisteViolazioneInAttesaPerTeam(Long idTeam) {
        return this.violazioneRepository.existsByTeam_CodiceTeamAndStatoProvvedimento(idTeam, StatoViolazione.IN_ATTESA);
    }

    public List<Violazione> trovaPerHackathon(Long idHackathon) {
        return this.violazioneRepository.findByTeam_Hackathon_Id(idHackathon);
    }

    public java.util.Optional<Violazione> trovaPerId(Long id) {
        return this.violazioneRepository.findById(id);
    }
}