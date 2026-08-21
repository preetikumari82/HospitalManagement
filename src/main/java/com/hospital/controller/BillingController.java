package com.hospital.controller;
import java.util.*; import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import com.hospital.Dto.*; import com.hospital.enums.PaymentStatus; import com.hospital.service.BillingService;
@RestController @RequestMapping("/api/bills") public class BillingController{
 private final BillingService s; public BillingController(BillingService s){this.s=s;}
 @GetMapping public List<BillResponse> all(@RequestParam(required=false) PaymentStatus status,@RequestParam(required=false) Long patientId){return s.all(status,patientId);}
 @GetMapping("/{id}") public BillResponse get(@PathVariable Long id){return s.get(id);}
 @PostMapping @PreAuthorize("hasAnyRole('RECEPTIONIST','ADMIN','MEDICAL')") public ResponseEntity<BillResponse> create(@RequestBody BillRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(s.create(r));}
 @PutMapping("/{id}/pay") @PreAuthorize("hasAnyRole('RECEPTIONIST','ADMIN','MEDICAL','PATIENT')") public BillResponse pay(@PathVariable Long id,@RequestBody(required=false) PaymentRequest r){return s.pay(id,r==null?new PaymentRequest():r);}
 @GetMapping(value="/{id}/invoice",produces=MediaType.APPLICATION_PDF_VALUE) public ResponseEntity<byte[]> invoice(@PathVariable Long id){return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=invoice-"+id+".pdf").body(s.invoice(id));}
}
