package com.hospital.Dto;
import java.time.*; import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor public class ScheduleRequest { private Long doctorId; private DayOfWeek dayOfWeek; private LocalTime startTime; private LocalTime endTime; private Integer slotMinutes=30; private Boolean active=true; }
