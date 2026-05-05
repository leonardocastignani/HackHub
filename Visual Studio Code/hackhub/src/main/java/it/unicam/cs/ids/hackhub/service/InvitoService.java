package it.unicam.cs.ids.hackhub.service;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.model.enums.*;
import it.unicam.cs.ids.hackhub.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class InvitoService {

    private final InvitoRepository invitoRepository;

    public InvitoService(InvitoRepository invitoRepository) {
        this.invitoRepository = invitoRepository;
    }

    public Invito salvaInvito(Invito invito) {
        return this.invitoRepository.save(invito);
    }

    public Optional<Invito> trovaPerId(Long id) {
        return this.invitoRepository.findById(id);
    }

    public boolean esisteInvitoInAttesa(String utenteCodiceFiscale, Long teamId) {
        return this.invitoRepository.existsByDestinatario_CodiceFiscaleAndMittente_Team_CodiceTeamAndStato(
                utenteCodiceFiscale,
                teamId,
                StatoInvito.IN_ATTESA
        );
    }
}