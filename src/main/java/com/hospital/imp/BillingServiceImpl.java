package com.hospital.imp;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.Dto.BillItemRequest;
import com.hospital.Dto.BillItemResponse;
import com.hospital.Dto.BillRequest;
import com.hospital.Dto.BillResponse;
import com.hospital.Dto.PaymentRequest;
import com.hospital.enums.PaymentStatus;
import com.hospital.model.Appointment;
import com.hospital.model.Bill;
import com.hospital.model.BillItem;
import com.hospital.model.Patient;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.BillRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.service.BillingService;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class BillingServiceImpl implements BillingService {

    private final BillRepository bills;
    private final PatientRepository patients;
    private final AppointmentRepository appointments;

    public BillingServiceImpl(BillRepository bills, PatientRepository patients, AppointmentRepository appointments) {
        this.bills = bills;
        this.patients = patients;
        this.appointments = appointments;
    }

    @Override
    @Transactional
    public BillResponse create(BillRequest r) {
        Patient p = patients.findById(r.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Appointment a = r.getAppointmentId() == null ? null :
                appointments.findById(r.getAppointmentId())
                        .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (r.getItems() == null || r.getItems().isEmpty()) {
            throw new RuntimeException("At least one bill item is required");
        }

        Bill b = Bill.builder().patient(p).appointment(a).build();
        double total = 0;

        for (BillItemRequest x : r.getItems()) {
            if (x.getAmount() == null || x.getAmount() < 0) {
                throw new RuntimeException("Invalid bill amount");
            }
            int q = x.getQuantity() == null || x.getQuantity() < 1 ? 1 : x.getQuantity();
            double line = x.getAmount() * q;
            BillItem item = BillItem.builder()
                    .bill(b)
                    .itemType(x.getItemType())
                    .description(x.getDescription())
                    .amount(x.getAmount())
                    .quantity(q)
                    .build();
            b.getItems().add(item);
            total += line;
        }

        b.setTotalAmount(total);
        b.setPaidAmount(0.0);
        b.setStatus(total == 0 ? PaymentStatus.PAID : PaymentStatus.PENDING);

        return map(bills.save(b));
    }

    @Override
    public List<BillResponse> all(PaymentStatus status, Long patientId) {
        List<Bill> list;
        if (patientId != null && status != null) {
            list = bills.findByPatientIdOrderByCreatedAtDesc(patientId).stream()
                    .filter(b -> b.getStatus() == status)
                    .toList();
        } else if (patientId != null) {
            list = bills.findByPatientIdOrderByCreatedAtDesc(patientId);
        } else if (status != null) {
            list = bills.findByStatusOrderByCreatedAtDesc(status);
        } else {
            list = bills.findAll().stream()
                    .sorted(Comparator.comparing(Bill::getCreatedAt).reversed())
                    .toList();
        }
        return list.stream().map(this::map).toList();
    }

    @Override
    public BillResponse get(Long id) {
        return map(bills.findById(id).orElseThrow(() -> new RuntimeException("Bill not found")));
    }

    @Override
    @Transactional
    public BillResponse pay(Long id, PaymentRequest r) {
        Bill b = bills.findById(id).orElseThrow(() -> new RuntimeException("Bill not found"));
        double outstanding = b.getTotalAmount() - b.getPaidAmount();
        double amount = (r == null || r.getAmount() == null) ? outstanding : r.getAmount();

        if (amount <= 0) {
            throw new RuntimeException("Payment amount must be greater than zero");
        }
        if (amount > outstanding + 0.01) { // tolerance for float
            throw new RuntimeException("Payment exceeds outstanding balance of ₹" + String.format("%.2f", outstanding));
        }

        b.setPaidAmount(b.getPaidAmount() + amount);
        b.setStatus(b.getPaidAmount() >= b.getTotalAmount() - 0.01 ? PaymentStatus.PAID : PaymentStatus.PARTIALLY_PAID);
        return map(bills.save(b));
    }

    @Override
    public byte[] invoice(Long id) {
        Bill b = bills.findById(id).orElseThrow(() -> new RuntimeException("Bill not found"));
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            doc.add(new Paragraph("MERIDIAN CARE - HOSPITAL", title));
            doc.add(new Paragraph("Invoice / Official Receipt #" + b.getId()));
            String patientName = b.getPatient().getUser() != null ? b.getPatient().getUser().getName() : "Patient #" + b.getPatient().getId();
            doc.add(new Paragraph("Patient: " + patientName + " | Patient ID: " + b.getPatient().getId()));
            doc.add(new Paragraph("Date: " + b.getCreatedAt()));
            doc.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);
            String[] h = {"Type", "Description", "Qty", "Rate (₹)", "Amount (₹)"};
            for (String s : h) {
                table.addCell(s);
            }
            for (BillItem i : b.getItems()) {
                table.addCell(i.getItemType() != null ? i.getItemType().name() : "ITEM");
                table.addCell(i.getDescription() != null ? i.getDescription() : "");
                table.addCell(String.valueOf(i.getQuantity() != null ? i.getQuantity() : 1));
                table.addCell(String.format("%.2f", i.getAmount() != null ? i.getAmount() : 0.0));
                double lineTotal = (i.getAmount() != null ? i.getAmount() : 0.0) * (i.getQuantity() != null ? i.getQuantity() : 1);
                table.addCell(String.format("%.2f", lineTotal));
            }
            doc.add(table);
            doc.add(Chunk.NEWLINE);

            doc.add(new Paragraph(String.format("Total Amount: ₹ %.2f", b.getTotalAmount())));
            doc.add(new Paragraph(String.format("Paid Amount: ₹ %.2f", b.getPaidAmount())));
            doc.add(new Paragraph(String.format("Outstanding Balance: ₹ %.2f", Math.max(0.0, b.getTotalAmount() - b.getPaidAmount()))));
            doc.add(new Paragraph("Payment Status: " + b.getStatus()));
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Unable to generate PDF invoice", e);
        }
    }

    private BillResponse map(Bill b) {
        List<BillItemResponse> items = b.getItems().stream().map(i -> BillItemResponse.builder()
                .id(i.getId())
                .itemType(i.getItemType())
                .description(i.getDescription())
                .amount(i.getAmount())
                .quantity(i.getQuantity())
                .build()).toList();

        String patientName = (b.getPatient() != null && b.getPatient().getUser() != null)
                ? b.getPatient().getUser().getName()
                : "Patient #" + (b.getPatient() != null ? b.getPatient().getId() : "?");

        return BillResponse.builder()
                .id(b.getId())
                .patientId(b.getPatient() != null ? b.getPatient().getId() : null)
                .appointmentId(b.getAppointment() == null ? null : b.getAppointment().getId())
                .patientName(patientName)
                .totalAmount(b.getTotalAmount())
                .paidAmount(b.getPaidAmount())
                .balanceAmount(Math.max(0.0, b.getTotalAmount() - b.getPaidAmount()))
                .status(b.getStatus())
                .createdAt(b.getCreatedAt())
                .items(items)
                .build();
    }
}
