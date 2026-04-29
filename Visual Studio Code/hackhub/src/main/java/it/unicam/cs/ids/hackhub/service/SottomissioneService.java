package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class SottomissioneService {

    private final SottomissioneRepository sottomissioneRepository;

    public SottomissioneService(SottomissioneRepository sottomissioneRepository) {
        this.sottomissioneRepository = sottomissioneRepository;
    }

    public Sottomissione salvaSottomissione(Sottomissione sottomissione) {
        return sottomissioneRepository.save(sottomissione);
    }

    public Optional<Sottomissione> trovaPerId(Long id) {
        return sottomissioneRepository.findById(id);
    }

    public List<Sottomissione> trovaPerHackathon(Long idHackathon) {
        return sottomissioneRepository.findByTeam_Hackathon_Id(idHackathon);
    }
}