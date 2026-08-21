package com.hospital.imp;

import com.hospital.Dto.DoctorLeaveRequest;
import com.hospital.Dto.DoctorLeaveResponse;
import com.hospital.model.Doctor;
import com.hospital.model.DoctorLeave;
import com.hospital.repository.DocterRepository;
import com.hospital.repository.DoctorLeaveRepository;

import com.hospital.service.DoctorLeaveService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorLeaveServiceImpl
        implements DoctorLeaveService {

    private final DoctorLeaveRepository doctorLeaveRepository;

    private final DocterRepository doctorRepository;

    public DoctorLeaveServiceImpl(
            DoctorLeaveRepository doctorLeaveRepository,
            DocterRepository doctorRepository) {

        this.doctorLeaveRepository = doctorLeaveRepository;
        this.doctorRepository = doctorRepository;
    }

    // ============================
    // CREATE LEAVE
    // ============================

    @Override
    public DoctorLeaveResponse createLeave(
            DoctorLeaveRequest request) {

        if (request.getDoctorId() == null) {
            throw new RuntimeException(
                    "Doctor ID is required"
            );
        }

        if (request.getLeaveDate() == null) {
            throw new RuntimeException(
                    "Leave date is required"
            );
        }

        Doctor doctor = doctorRepository
                .findById(request.getDoctorId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Doctor not found with id: "
                                        + request.getDoctorId()
                        ));

        // Check duplicate leave
        if (doctorLeaveRepository
                .existsByDoctorIdAndLeaveDate(
                        request.getDoctorId(),
                        request.getLeaveDate())) {

            throw new RuntimeException(
                    "Doctor already has leave on this date"
            );
        }

        DoctorLeave leave = new DoctorLeave();

        leave.setDoctor(doctor);
        leave.setLeaveDate(request.getLeaveDate());
        leave.setReason(request.getReason());

        if (request.getApproved() != null) {
            leave.setApproved(request.getApproved());
        } else {
            leave.setApproved(true);
        }

        DoctorLeave savedLeave =
                doctorLeaveRepository.save(leave);

        return convertToResponse(savedLeave);
    }

    // ============================
    // GET ALL LEAVES
    // ============================

    @Override
    public List<DoctorLeaveResponse> getAllLeaves() {

        return doctorLeaveRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ============================
    // GET LEAVE BY ID
    // ============================

    @Override
    public DoctorLeaveResponse getLeaveById(Long id) {

        DoctorLeave leave =
                doctorLeaveRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Leave not found with id: "
                                                + id
                                ));

        return convertToResponse(leave);
    }

    // ============================
    // GET DOCTOR LEAVES
    // ============================

    @Override
    public List<DoctorLeaveResponse> getLeavesByDoctor(
            Long doctorId) {

        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException(
                    "Doctor not found with id: "
                            + doctorId
            );
        }

        return doctorLeaveRepository
                .findByDoctorId(doctorId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ============================
    // UPDATE LEAVE
    // ============================

    @Override
    public DoctorLeaveResponse updateLeave(
            Long id,
            DoctorLeaveRequest request) {

        DoctorLeave existingLeave =
                doctorLeaveRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Leave not found with id: "
                                                + id
                                ));

        if (request.getLeaveDate() == null) {
            throw new RuntimeException(
                    "Leave date is required"
            );
        }

        // If date changed, check duplicate
        if (!existingLeave.getLeaveDate()
                .equals(request.getLeaveDate())) {

            if (doctorLeaveRepository
                    .existsByDoctorIdAndLeaveDate(
                            existingLeave.getDoctor().getId(),
                            request.getLeaveDate())) {

                throw new RuntimeException(
                        "Doctor already has leave on this date"
                );
            }

            existingLeave.setLeaveDate(
                    request.getLeaveDate()
            );
        }

        existingLeave.setReason(
                request.getReason()
        );

        if (request.getApproved() != null) {
            existingLeave.setApproved(
                    request.getApproved()
            );
        }

        DoctorLeave updatedLeave =
                doctorLeaveRepository.save(
                        existingLeave
                );

        return convertToResponse(updatedLeave);
    }

    // ============================
    // DELETE LEAVE
    // ============================

    @Override
    public void deleteLeave(Long id) {

        DoctorLeave leave =
                doctorLeaveRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Leave not found with id: "
                                                + id
                                ));

        doctorLeaveRepository.delete(leave);
    }

    // ============================
    // CONVERT ENTITY TO RESPONSE
    // ============================

    private DoctorLeaveResponse convertToResponse(
            DoctorLeave leave) {

        DoctorLeaveResponse response =
                new DoctorLeaveResponse();

        response.setId(leave.getId());

        response.setDoctorId(
                leave.getDoctor().getId()
        );

        if (leave.getDoctor().getUser() != null) {

            response.setDoctorName(
                    leave.getDoctor()
                            .getUser()
                            .getName()
            );
        }

        response.setLeaveDate(
                leave.getLeaveDate()
        );

        response.setReason(
                leave.getReason()
        );

        response.setApproved(
                leave.isApproved()
        );

        return response;
    }
}