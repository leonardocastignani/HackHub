package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class CallMentoringService {

    private final CallMentoringRepository callMentoringRepository;

    public CallMentoringService(CallMentoringRepository callMentoringRepository) {
        this.callMentoringRepository = callMentoringRepository;
    }

    public CallMentoring salvaCall(CallMentoring call) {
        return this.callMentoringRepository.save(call);
    }

    public List<CallMentoring> visualizzaElencoCallTeam(Long idTeam) {
        return this.callMentoringRepository.findByTeam_CodiceTeam(idTeam);
    }

    public Optional<CallMentoring> trovaPerId(Long idCall) {
        return this.callMentoringRepository.findById(idCall);
    }
}