package com.hospital.imp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hospital.Dto.MedicineItemRequest;
import com.hospital.Dto.MedicineItemResponse;
import com.hospital.Dto.MedicineRequest;
import com.hospital.Dto.MedicineResponse;
import com.hospital.Dto.PatientRequest;
import com.hospital.Dto.PatientResponse;
import com.hospital.Dto.ReceiptResponse;
import com.hospital.Dto.StatusRequest;
import com.hospital.Dto.TreatmentRequest;
import com.hospital.model.Medicine;
import com.hospital.model.Patient;
import com.hospital.repository.MedicineRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.service.NurseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public  class NurseServiceImpl implements NurseService {

    private final PatientRepository patientRepository;
    private final MedicineRepository medicineRepository;

    @Override
    public List<PatientResponse> getAllPatients() {

        return patientRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PatientResponse getPatientById(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient Not Found"));

        return convertToResponse(patient);
    }
    @Override
    public PatientResponse updateTreatment(Long id, TreatmentRequest request) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient Not Found"));

        patient.setTreatment(request.getTreatment());
      
        patient.setNurseRemarks(request.getNurseRemarks());

        Patient updatedPatient = patientRepository.save(patient);

        return convertToResponse(updatedPatient);
    }


    @Override
    public PatientResponse updateStatus(Long id, StatusRequest request) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient Not Found"));

        patient.setStatus(request.getStatus());

        Patient updatedPatient = patientRepository.save(patient);

        return convertToResponse(updatedPatient);
    }
    @Override
     public MedicineResponse addMedicine(Long patientId, MedicineRequest request) {

    	Patient patient = patientRepository.findById(patientId)
    	        .orElseThrow(() -> new RuntimeException("Patient Not Found"));

    	List<MedicineItemResponse> medicineResponses = new ArrayList<>();

    	for (MedicineItemRequest item : request.getMedicines()) {

    	    Medicine medicine = Medicine.builder()
    	            .medicineName(item.getMedicineName())
    	            .dosage(item.getDosage())
    	            .timing(item.getTiming())
    	            .patient(patient)
    	            .build();

    	    Medicine saved = medicineRepository.save(medicine);

    	    medicineResponses.add(
    	            MedicineItemResponse.builder()
    	                    .medicineName(saved.getMedicineName())
    	                    .dosage(saved.getDosage())
    	                    .timing(saved.getTiming())
    	                    .build()
    	    );
    	}

    	return MedicineResponse.builder()
    	        .patient(patient.getUser().getName())
    	        .doctor(request.getDoctor())
    	        .date(request.getDate())
    	        .medicines(medicineResponses)
    	        .build();
    }
    @Override
    public String createReceipt(Long patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient Not Found"));

        List<Medicine> medicines = medicineRepository.findByPatientId(patientId);

        StringBuilder receipt = new StringBuilder();

        receipt.append("-------------------------------------\n");
        receipt.append("         XYZ HOSPITAL\n");
        receipt.append("-------------------------------------\n");
        receipt.append("Receipt No : RCP-" + patient.getId() + "\n\n");
        receipt.append("Patient Id   : " + patient.getId() + "\n");
        receipt.append("Patient Name : " + patient.getUser().getName() + "\n");
        receipt.append("Disease      : " + patient.getDisease() + "\n");
        receipt.append("Treatment    : " + patient.getTreatment() + "\n\n");

        receipt.append("Medicines:\n");

        if (medicines.isEmpty()) {
            receipt.append("No medicines prescribed.\n");
        } else {
            int i = 1;
            for (Medicine medicine : medicines) {
                receipt.append(i++)
                       .append(". ")
                       .append(medicine.getMedicineName())
                       .append(" | Dosage: ")
                       .append(medicine.getDosage())
                       .append(" | Timing: ")
                       .append(medicine.getTiming())
                       .append("\n");
            }
        }

        receipt.append("\n");
        receipt.append("Remarks      : ").append(patient.getNurseRemarks()).append("\n");
        receipt.append("Status       : ").append(patient.getStatus()).append("\n");
        receipt.append("Fees         : ₹").append(patient.getFees()).append("\n");
        receipt.append("Date         : ").append(LocalDate.now()).append("\n");
        receipt.append("Generated By : Nurse\n");
        receipt.append("-------------------------------------");

        return receipt.toString();
    }
 
   
   

    private PatientResponse convertToResponse(Patient patient) {

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

	