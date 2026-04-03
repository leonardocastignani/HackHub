package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class MembroDelTeamService {

    private final MembroDelTeamRepository membroDelTeamRepository;

    public MembroDelTeamService(MembroDelTeamRepository membroDelTeamRepository) {
        this.membroDelTeamRepository = membroDelTeamRepository;
    }

    public Optional<MembroDelTeam> trovaPerCodiceFiscale(String codiceFiscale) {
        return this.membroDelTeamRepository.findById(codiceFiscale);
    }

    public MembroDelTeam salvaMembro(MembroDelTeam membro) {
        return this.membroDelTeamRepository.save(membro);
    }
}