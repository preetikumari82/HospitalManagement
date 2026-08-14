package com.hospital.imp;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.Dto.MedicineStockRequest;
import com.hospital.Dto.MedicineStockResponse;
import com.hospital.model.MedicineStock;
import com.hospital.repository.MedicineStockRepository;
import com.hospital.service.MedicineStockService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicineStockServiceImpl implements MedicineStockService {

    private final MedicineStockRepository medicineStockRepository;

    // ------------------------------------------------
    // ADD NEW MEDICINE STOCK
    // ------------------------------------------------

    @Override
    public MedicineStockResponse addStock(MedicineStockRequest request) {

        MedicineStock existing =
                medicineStockRepository
                        .findByMedicineNameIgnoreCase(
                                request.getMedicineName())
                        .orElse(null);

        if (existing != null) {
            throw new RuntimeException(
                    "Medicine already exists in stock");
        }

        MedicineStock stock = MedicineStock.builder()
                .medicineName(request.getMedicineName())
                .availableQuantity(request.getAvailableQuantity())
                .rate(request.getRate())
                .build();

        MedicineStock saved =
                medicineStockRepository.save(stock);

        return convertToResponse(saved);
    }

    // ------------------------------------------------
    // GET ALL STOCK
    // ------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<MedicineStockResponse> getAllStock() {

        return medicineStockRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------
    // GET STOCK BY ID
    // ------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public MedicineStockResponse getStockById(Long id) {

        MedicineStock stock =
                getStockOrThrow(id);

        return convertToResponse(stock);
    }

    // ------------------------------------------------
    // UPDATE COMPLETE STOCK
    // ------------------------------------------------

    @Override
    public MedicineStockResponse updateStock(
            Long id,
            MedicineStockRequest request) {

        MedicineStock stock =
                getStockOrThrow(id);

        stock.setMedicineName(
                request.getMedicineName());

        stock.setAvailableQuantity(
                request.getAvailableQuantity());

        stock.setRate(
                request.getRate());

        MedicineStock updated =
                medicineStockRepository.save(stock);

        return convertToResponse(updated);
    }

    // ------------------------------------------------
    // ADD QUANTITY
    // ------------------------------------------------

    @Override
    public MedicineStockResponse addQuantity(
            Long id,
            Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero");
        }

        MedicineStock stock =
                getStockOrThrow(id);

        int newQuantity =
                stock.getAvailableQuantity() + quantity;

        stock.setAvailableQuantity(newQuantity);

        MedicineStock updated =
                medicineStockRepository.save(stock);

        return convertToResponse(updated);
    }

    // ------------------------------------------------
    // REDUCE QUANTITY
    // ------------------------------------------------

    @Override
    public MedicineStockResponse reduceQuantity(
            Long id,
            Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero");
        }

        MedicineStock stock =
                getStockOrThrow(id);

        if (stock.getAvailableQuantity() < quantity) {
            throw new RuntimeException(
                    "Insufficient medicine stock");
        }

        int newQuantity =
                stock.getAvailableQuantity() - quantity;

        stock.setAvailableQuantity(newQuantity);

        MedicineStock updated =
                medicineStockRepository.save(stock);

        return convertToResponse(updated);
    }

    // ------------------------------------------------
    // CHECK AVAILABILITY
    // ------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public MedicineStockResponse checkAvailability(Long id) {

        MedicineStock stock =
                getStockOrThrow(id);

        return convertToResponse(stock);
    }

    // ------------------------------------------------
    // DELETE
    // ------------------------------------------------

    @Override
    public void deleteStock(Long id) {

        MedicineStock stock =
                getStockOrThrow(id);

        medicineStockRepository.delete(stock);
    }

    // ------------------------------------------------
    // HELPER
    // ------------------------------------------------

    private MedicineStock getStockOrThrow(Long id) {

        return medicineStockRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Medicine stock not found"));
    }

    private MedicineStockResponse convertToResponse(
            MedicineStock stock) {

        boolean available =
                stock.getAvailableQuantity() != null
                        && stock.getAvailableQuantity() > 0;

        return MedicineStockResponse.builder()
                .id(stock.getId())
                .medicineName(stock.getMedicineName())
                .availableQuantity(
                        stock.getAvailableQuantity())
                .rate(stock.getRate())
                .available(available)
                .build();
    }
}