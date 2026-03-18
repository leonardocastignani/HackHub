package it.unicam.cs.ids.hackhub.controller;

import it.unicam.cs.ids.hackhub.model.*;
import it.unicam.cs.ids.hackhub.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/hackathons")
public class HackathonController {

    @Autowired
    private HackathonService hackathonService;

    @GetMapping
    public List<Hackathon> getHackathons() {
        return hackathonService.ottieniTuttiHackathon();
    }

    @PostMapping
    public Hackathon nuovoHackathon(@RequestBody Hackathon hackathon) {
        return hackathonService.creaHackathon(hackathon);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hackathon> getHackathon(@PathVariable Long id) {
        return hackathonService.ottieniHackathon(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}