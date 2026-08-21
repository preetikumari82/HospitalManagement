package com.hospital.Dto;
import java.time.*; import lombok.*;
@Getter @Setter @Builder public class ScheduleResponse { private Long id, doctorId; private DayOfWeek dayOfWeek; private LocalTime startTime,endTime; private Integer slotMinutes; private boolean active; }
