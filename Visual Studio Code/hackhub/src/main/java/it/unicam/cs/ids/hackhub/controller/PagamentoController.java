package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.dto.*;
import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.model.enums.*;
import it.unicam.cs.ids.hackhub.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamenti")
public class PagamentoController {

    private final HackHubSystem hackHubSystem;

    public PagamentoController(HackHubSystem hackHubSystem) {
        this.hackHubSystem = hackHubSystem;
    }

    // =================================
    // ENDPOINT: Avvia Pagamento Premio
    // =================================
    @PostMapping("/avvia")
    public ResponseEntity<?> avviaPagamento(@RequestBody AvviaPagamentoRequest request) {
        Pagamento esitoPagamento = this.hackHubSystem.avviaPagamentoPremio(
            request.idHackathon(),
            request.codiceFiscaleOrganizzatore()
        );

        if (esitoPagamento.getStato() == StatoPagamento.FALLITO) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(esitoPagamento);
        }

        return ResponseEntity.ok(esitoPagamento);
    }
}