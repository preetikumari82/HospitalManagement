package com.hospital.imp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hospital.Dto.MedicineItemRequest;
import com.hospital.Dto.MedicineItemResponse;
import com.hospital.Dto.MedicineRateUpdateRequest;
import com.hospital.Dto.MedicineRequest;
import com.hospital.Dto.MedicineResponse;
import com.hospital.Dto.PatientResponse;
import com.hospital.model.Medicine;
import com.hospital.model.Patient;
import com.hospital.repository.MedicineRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.service.MedicalService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicalServiceImpl implements MedicalService {

    private final PatientRepository patientRepository;
    private final MedicineRepository medicineRepository;

    @Override
    public List<PatientResponse> getAllPatients() {

        return patientRepository.findAll()
                .stream()
                .map(this::convertToPatientResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PatientResponse getPatientById(Long id) {

        Patient patient = getPatientOrThrow(id);
        return convertToPatientResponse(patient);
    }

    @Override
    public MedicineResponse addMedicine(Long patientId, MedicineRequest request) {

        Patient patient = getPatientOrThrow(patientId);

        if (request.getMedicines() == null || request.getMedicines().isEmpty()) {
            throw new RuntimeException("At least one medicine is required");
        }

        for (MedicineItemRequest item : request.getMedicines()) {

            Medicine medicine = Medicine.builder()
                    .medicineName(item.getMedicineName())
                    .dosage(item.getDosage())
                    .timing(item.getTiming())
                    .quantity(item.getQuantity() != null ? item.getQuantity() : 1)
                    .rate(item.getRate() != null ? item.getRate() : 0.0)
                    .patient(patient)
                    .build();

            // total is auto calculated inside Medicine's @PrePersist
            medicineRepository.save(medicine);
        }

        // Return the patient's full, up-to-date medicine list + auto-summed grand total
        return buildMedicineResponse(patient, request.getDoctor(), request.getDate());
    }

    @Override
    public MedicineItemResponse updateMedicineRate(Long medicineId, MedicineRateUpdateRequest request) {

        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new RuntimeException("Medicine record not found"));

        if (request.getRate() == null) {
            throw new RuntimeException("Rate is required");
        }

        medicine.setRate(request.getRate());

        if (request.getQuantity() != null) {
            medicine.setQuantity(request.getQuantity());
        }

        // total is auto recalculated inside Medicine's @PreUpdate
        Medicine updated = medicineRepository.save(medicine);

        return toItemResponse(updated);
    }

    @Override
    public MedicineResponse getMedicineBill(Long patientId) {

        Patient patient = getPatientOrThrow(patientId);
        return buildMedicineResponse(patient, null, LocalDate.now());
    }

    @Override
    public String generateReceipt(Long patientId) {

        Patient patient = getPatientOrThrow(patientId);
        List<Medicine> medicines = medicineRepository.findByPatientIdOrderByIdAsc(patientId);

        double medicineTotal = medicines.stream()
                .mapToDouble(m -> m.getTotal() != null ? m.getTotal() : 0.0)
                .sum();

        StringBuilder receipt = new StringBuilder();

        receipt.append("-------------------------------------\n");
        receipt.append("         XYZ HOSPITAL - PHARMACY\n");
        receipt.append("-------------------------------------\n");
        receipt.append("Receipt No   : MED-" + patient.getId() + "\n\n");
        receipt.append("Patient Id   : " + patient.getId() + "\n");
        receipt.append("Patient Name : " + patient.getUser().getName() + "\n");
        receipt.append("Disease      : " + patient.getDisease() + "\n\n");

        receipt.append("Medicines:\n");

        if (medicines.isEmpty()) {
            receipt.append("No medicines prescribed.\n");
        } else {
            int i = 1;
            for (Medicine medicine : medicines) {
                receipt.append(i++)
                        .append(". ")
                        .append(medicine.getMedicineName())
                        .append(" | Qty: ").append(medicine.getQuantity())
                        .append(" | Rate: Rs.").append(medicine.getRate())
                        .append(" | Total: Rs.").append(medicine.getTotal())
                        .append("\n");
            }
        }

        receipt.append("\n");
        receipt.append("Medicine Total : Rs.").append(medicineTotal).append("\n");
        receipt.append("Consultation Fees : Rs.").append(patient.getFees()).append("\n");
        receipt.append("Grand Total    : Rs.").append(medicineTotal + patient.getFees()).append("\n");
        receipt.append("Date           : ").append(LocalDate.now()).append("\n");
        receipt.append("Generated By   : Medical\n");
        receipt.append("-------------------------------------");

        return receipt.toString();
    }

    // ---------- helpers ----------

    private Patient getPatientOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient Not Found"));
    }

    private MedicineResponse buildMedicineResponse(Patient patient, String doctor, LocalDate date) {

        List<Medicine> medicines = medicineRepository.findByPatientIdOrderByIdAsc(patient.getId());

        List<MedicineItemResponse> itemResponses = new ArrayList<>();
        double grandTotal = 0.0;

        for (Medicine medicine : medicines) {
            itemResponses.add(toItemResponse(medicine));
            grandTotal += medicine.getTotal() != null ? medicine.getTotal() : 0.0;
        }

        return MedicineResponse.builder()
                .patientId(patient.getId())
                .patient(patient.getUser().getName())
                .doctor(doctor)
                .date(date != null ? date : LocalDate.now())
                .medicines(itemResponses)
                .grandTotal(grandTotal)
                .build();
    }

    private MedicineItemResponse toItemResponse(Medicine medicine) {
        return MedicineItemResponse.builder()
                .id(medicine.getId())
                .medicineName(medicine.getMedicineName())
                .dosage(medicine.getDosage())
                .timing(medicine.getTiming())
                .quantity(medicine.getQuantity())
                .rate(medicine.getRate())
                .total(medicine.getTotal())
                .build();
    }

    private PatientResponse convertToPatientResponse(Patient patient) {

        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getUser().getName())
                .email(patient.getUser().getEmail())
                .age(patient.getAge())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .fees(patient.getFees())
                .disease(patient.getDisease())
                .treatment(patient.getTreatment())
                .nurseRemarks(patient.getNurseRemarks())
                .status(patient.getStatus())
                .admissionDate(patient.getAdmissionDate())
                .dischargeDate(patient.getDischargeDate())
                .role(patient.getUser().getRole().name())
                .build();
    }
}
