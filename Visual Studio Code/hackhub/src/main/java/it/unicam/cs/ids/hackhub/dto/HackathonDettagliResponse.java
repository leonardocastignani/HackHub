package it.unicam.cs.ids.hackhub.dto;

import it.unicam.cs.ids.hackhub.model.*;
import java.util.*;

public record HackathonDettagliResponse(
        Hackathon hackathon,
        List<String> azioniDisponibili
) {}