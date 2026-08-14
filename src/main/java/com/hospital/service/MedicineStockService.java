package com.hospital.service;

import java.util.List;

import com.hospital.Dto.MedicineStockRequest;
import com.hospital.Dto.MedicineStockResponse;

public interface MedicineStockService {

    // Add new medicine stock
    MedicineStockResponse addStock(MedicineStockRequest request);

    // Get all medicine stock
    List<MedicineStockResponse> getAllStock();

    // Get stock by ID
    MedicineStockResponse getStockById(Long id);

    // Update complete stock information
    MedicineStockResponse updateStock(
            Long id,
            MedicineStockRequest request);

    // Add quantity to existing stock
    MedicineStockResponse addQuantity(
            Long id,
            Integer quantity);

    // Reduce quantity when medicine is issued
    MedicineStockResponse reduceQuantity(
            Long id,
            Integer quantity);

    // Check medicine availability
    MedicineStockResponse checkAvailability(Long id);

    // Delete medicine from stock
    void deleteStock(Long id);
}