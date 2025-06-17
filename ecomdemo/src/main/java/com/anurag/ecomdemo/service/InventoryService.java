package com.anurag.ecomdemo.service;

import com.anurag.ecomdemo.model.Inventory;
import com.anurag.ecomdemo.model.Item;
import com.anurag.ecomdemo.model.Reservation;
import com.anurag.ecomdemo.model.ReservationStatus;
import com.anurag.ecomdemo.repository.InventoryRepository;
import com.anurag.ecomdemo.repository.ItemRepository;
import com.anurag.ecomdemo.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepo;
    private final ItemRepository itemRepo;
    private final ReservationRepository reservationRepo;
    private final RedisTemplate<String, String> redisTemplate;

    private final String CACHE_KEY_PREFIX = "item:avail:";

    public void addSupply(Long itemId, int quantity) {
        Inventory inventory = inventoryRepo.findByItemId(itemId)
            .orElseGet(() -> {
                Item item = itemRepo.findById(itemId).orElseThrow();
                return new Inventory(item, 0, 0);
            });

        inventory.setTotalQuantity(inventory.getTotalQuantity() + quantity);
        inventoryRepo.save(inventory);
        updateCache(itemId, inventory);
    }

    public void reserve(Long itemId, int quantity, String reservedBy) {
        Inventory inventory = inventoryRepo.lockByItemId(itemId);
        int available = inventory.getTotalQuantity() - inventory.getReservedQuantity();
        if (quantity > available) throw new IllegalStateException("Insufficient stock");

        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        inventoryRepo.save(inventory);

        Reservation res = new Reservation();
        res.setItem(inventory.getItem());
        res.setQuantity(quantity);
        res.setReservedBy(reservedBy);
        res.setStatus(ReservationStatus.ACTIVE);
        res.setReservedAt(LocalDateTime.now());
        reservationRepo.save(res);

        updateCache(itemId, inventory);
    }

    public void cancelReservation(Long reservationId) {
        Reservation res = reservationRepo.findById(reservationId).orElseThrow();
        if (res.getStatus() == ReservationStatus.CANCELLED) return;

        Inventory inventory = inventoryRepo.lockByItemId(res.getItem().getId());
        inventory.setReservedQuantity(inventory.getReservedQuantity() - res.getQuantity());
        res.setStatus(ReservationStatus.CANCELLED);

        inventoryRepo.save(inventory);
        reservationRepo.save(res);
        updateCache(res.getItem().getId(), inventory);
    }

    public int getAvailable(Long itemId) {
        String cached = redisTemplate.opsForValue().get(CACHE_KEY_PREFIX + itemId);
        if (cached != null) return Integer.parseInt(cached);

        Inventory inv = inventoryRepo.findByItemId(itemId).orElseThrow();
        int available = inv.getTotalQuantity() - inv.getReservedQuantity();
        redisTemplate.opsForValue().set(CACHE_KEY_PREFIX + itemId, String.valueOf(available), 10, TimeUnit.SECONDS);
        return available;
    }

    private void updateCache(Long itemId, Inventory inventory) {
        int available = inventory.getTotalQuantity() - inventory.getReservedQuantity();
        redisTemplate.opsForValue().set(CACHE_KEY_PREFIX + itemId, String.valueOf(available), 10, TimeUnit.SECONDS);
    }
}
