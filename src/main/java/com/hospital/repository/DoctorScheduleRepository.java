package com.hospital.repository;
import java.time.DayOfWeek; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository; import com.hospital.model.DoctorSchedule;
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule,Long>{ List<DoctorSchedule> findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(Long doctorId); boolean existsByDoctorIdAndDayOfWeekAndStartTime(Long doctorId,DayOfWeek day,java.time.LocalTime time); }
