package com.kazmierczak.inventory_service.repository;

import com.kazmierczak.inventory_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
