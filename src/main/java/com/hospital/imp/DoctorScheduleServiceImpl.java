package com.hospital.imp;
import java.util.*; import org.springframework.stereotype.Service; import com.hospital.Dto.*; import com.hospital.model.*; import com.hospital.repository.*;
@Service public class DoctorScheduleServiceImpl implements com.hospital.service.DoctorScheduleService{
 private final DoctorScheduleRepository repo; private final DocterRepository doctors;
 public DoctorScheduleServiceImpl(DoctorScheduleRepository repo,DocterRepository doctors){this.repo=repo;this.doctors=doctors;}
 public ScheduleResponse create(ScheduleRequest r){validate(r); Doctor d=doctors.findById(r.getDoctorId()).orElseThrow(()->new RuntimeException("Doctor not found")); if(repo.existsByDoctorIdAndDayOfWeekAndStartTime(r.getDoctorId(),r.getDayOfWeek(),r.getStartTime()))throw new RuntimeException("Schedule slot already exists"); DoctorSchedule s=DoctorSchedule.builder().doctor(d).dayOfWeek(r.getDayOfWeek()).startTime(r.getStartTime()).endTime(r.getEndTime()).slotMinutes(r.getSlotMinutes()==null?30:r.getSlotMinutes()).active(r.getActive()==null||r.getActive()).build(); return map(repo.save(s)); }
 public List<ScheduleResponse> getByDoctor(Long id){return repo.findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(id).stream().map(this::map).toList();}
 public ScheduleResponse update(Long id,ScheduleRequest r){validate(r); DoctorSchedule s=repo.findById(id).orElseThrow(()->new RuntimeException("Schedule not found")); s.setDayOfWeek(r.getDayOfWeek());s.setStartTime(r.getStartTime());s.setEndTime(r.getEndTime());s.setSlotMinutes(r.getSlotMinutes()==null?30:r.getSlotMinutes());s.setActive(r.getActive()==null||r.getActive()); return map(repo.save(s));}
 public void delete(Long id){repo.delete(repo.findById(id).orElseThrow(()->new RuntimeException("Schedule not found")));}
 private void validate(ScheduleRequest r){if(r.getDoctorId()==null||r.getDayOfWeek()==null||r.getStartTime()==null||r.getEndTime()==null)throw new RuntimeException("Doctor, day, start time and end time are required");if(!r.getStartTime().isBefore(r.getEndTime()))throw new RuntimeException("Start time must be before end time");}
 private ScheduleResponse map(DoctorSchedule s){return ScheduleResponse.builder().id(s.getId()).doctorId(s.getDoctor().getId()).dayOfWeek(s.getDayOfWeek()).startTime(s.getStartTime()).endTime(s.getEndTime()).slotMinutes(s.getSlotMinutes()).active(s.isActive()).build();}
}
