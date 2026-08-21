package com.hospital.service;
import java.time.*; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Service; 
import com.hospital.config.EmailService; import com.hospital.enums.AppointmentStatus; import com.hospital.model.Appointment; 
import com.hospital.repository.AppointmentRepository;

@Service public class AppointmentReminderService { private final AppointmentRepository repo; private final EmailService email; 
public AppointmentReminderService(AppointmentRepository r,EmailService e){repo=r;email=e;} @Scheduled(cron="0 0 * * * *")
public void send24HourReminders(){LocalDate date=LocalDate.now().plusDays(1); for(Appointment 
		a:repo.findByAppointmentDateAndReminderSentFalse(date)){if(a.getStatus()
		==AppointmentStatus.CANCELLED)continue; String to=a.getPatient().getUser().getEmail(); String body=
		
		"Reminder: your hospital appointment with Dr. "+a.getDoctor().getUser().getName()+" is scheduled for "
				+ ""
				+ ""+a.getAppointmentDate()+" at "+a.getTimeSlot()+"."; email.sendPlainText(to,"Hospital Appointment Reminder",
						body);a.setReminderSent(true);repo.save(a);}} }
