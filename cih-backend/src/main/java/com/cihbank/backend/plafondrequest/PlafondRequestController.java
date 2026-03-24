package com.cihbank.backend.plafondrequest;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plafond-requests")
public class PlafondRequestController {

    private final PlafondRequestService service;

    public PlafondRequestController(PlafondRequestService service) {
        this.service = service;
    }

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public PlafondRequest create(
            @RequestParam Integer userId,
            @RequestParam Integer cardId,
            @RequestParam Double limit,
            @RequestParam String justification
    ) {
        return service.create(userId, cardId, limit, justification);
    }

    // =========================
    // GET ALL
    // =========================
    @GetMapping
    public List<PlafondRequest> getAll() {
        return service.getAll();
    }

    // =========================
    // GET BY USER
    // =========================
    @GetMapping("/user/{id}")
    public List<PlafondRequest> getByUser(@PathVariable Integer id) {
        return service.getByUser(id);
    }

    // =========================
    // GET ONE
    // =========================
    @GetMapping("/{id}")
    public PlafondRequest getOne(@PathVariable Integer id) {
        return service.getOne(id);
    }
    @GetMapping("/team/{idTeam}")
    public List<PlafondRequest> getByTeam(@PathVariable Integer idTeam) {
        return service.getByTeam(idTeam);
    }
}