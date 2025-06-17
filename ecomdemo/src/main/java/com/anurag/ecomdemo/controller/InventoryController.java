package com.anurag.ecomdemo.controller;
import com.anurag.ecomdemo.dto.CancelRequest;
import com.anurag.ecomdemo.dto.ReserveRequest;
import com.anurag.ecomdemo.dto.SupplyRequest;
import com.anurag.ecomdemo.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService service;

    @PostMapping("/supply")
    public ResponseEntity<String> supply(@RequestBody SupplyRequest req) {
        service.addSupply(req.getItemId(), req.getQuantity());
        return ResponseEntity.ok("Supply added");
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserve(@RequestBody ReserveRequest req) {
        service.reserve(req.getItemId(), req.getQuantity(), req.getReservedBy());
        return ResponseEntity.ok("Item reserved");
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancel(@RequestBody CancelRequest req) {
        service.cancelReservation(req.getReservationId());
        return ResponseEntity.ok("Reservation cancelled");
    }

    @GetMapping("/availability/{itemId}")
    public ResponseEntity<Integer> availability(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.getAvailable(itemId));
    }
}
