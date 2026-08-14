package com.hospital.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hospital.Dto.MedicineStockRequest;
import com.hospital.Dto.MedicineStockResponse;
import com.hospital.service.MedicineStockService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/medical/stock")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MEDICAL', 'PHARMACIST')")// FR1.2
public class MedicineStockController {

    private final MedicineStockService medicineStockService;

    // ---------------------------------------------
    // 1. ADD NEW MEDICINE STOCK
    // ---------------------------------------------

    @PostMapping
    public ResponseEntity<MedicineStockResponse> addStock(
            @Valid @RequestBody MedicineStockRequest request) {

        return ResponseEntity.ok(
                medicineStockService.addStock(request));
    }

    // ---------------------------------------------
    // 2. GET ALL STOCK
    // ---------------------------------------------

    @GetMapping
    public ResponseEntity<List<MedicineStockResponse>> getAllStock() {

        return ResponseEntity.ok(
                medicineStockService.getAllStock());
    }

    // ---------------------------------------------
    // 3. GET STOCK BY ID
    // ---------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<MedicineStockResponse> getStockById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                medicineStockService.getStockById(id));
    }

    // ---------------------------------------------
    // 4. UPDATE COMPLETE STOCK
    // ---------------------------------------------

    @PutMapping("/{id}")
    public ResponseEntity<MedicineStockResponse> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody MedicineStockRequest request) {

        return ResponseEntity.ok(
                medicineStockService.updateStock(
                        id, request));
    }

    // ---------------------------------------------
    // 5. ADD QUANTITY
    // ---------------------------------------------

    @PutMapping("/{id}/add-quantity")
    public ResponseEntity<MedicineStockResponse> addQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                medicineStockService.addQuantity(
                        id, quantity));
    }

    // ---------------------------------------------
    // 6. REDUCE QUANTITY
    // ---------------------------------------------

    @PutMapping("/{id}/reduce-quantity")
    public ResponseEntity<MedicineStockResponse> reduceQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                medicineStockService.reduceQuantity(
                        id, quantity));
    }

    // ---------------------------------------------
    // 7. CHECK AVAILABILITY
    // ---------------------------------------------

    @GetMapping("/{id}/availability")
    public ResponseEntity<MedicineStockResponse> checkAvailability(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                medicineStockService.checkAvailability(id));
    }

    // ---------------------------------------------
    // 8. DELETE STOCK
    // ---------------------------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStock(
            @PathVariable Long id) {

        medicineStockService.deleteStock(id);

        return ResponseEntity.ok(
                "Medicine stock deleted successfully");
    }
}