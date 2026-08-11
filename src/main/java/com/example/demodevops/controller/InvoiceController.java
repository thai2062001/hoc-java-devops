package com.example.demodevops.controller;

import com.example.demodevops.dto.ApiResponse;
import com.example.demodevops.dto.InvoiceCreateRequestDto;
import com.example.demodevops.dto.InvoiceDto;
import com.example.demodevops.security.EmployeePrincipal;
import com.example.demodevops.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<InvoiceDto>>> getAllInvoices() {
        List<InvoiceDto> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(ApiResponse.success(invoices, "Retrieved all invoices successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<InvoiceDto>> getInvoiceById(@PathVariable Long id) {
        InvoiceDto invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success(invoice, "Retrieved invoice details successfully"));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<InvoiceDto>>> getInvoicesByCustomer(@PathVariable Long customerId) {
        List<InvoiceDto> invoices = invoiceService.getInvoicesByCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success(invoices, "Retrieved customer invoices successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<InvoiceDto>> createInvoice(
            @RequestBody InvoiceCreateRequestDto request,
            @AuthenticationPrincipal EmployeePrincipal principal) {
        
        Long cashierId = principal != null ? principal.getEmployee().getId() : null;
        InvoiceDto created = invoiceService.createInvoiceFromAppointment(request, cashierId);
        return new ResponseEntity<>(ApiResponse.success(created, "Invoice created successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<InvoiceDto>> payInvoice(
            @PathVariable Long id,
            @AuthenticationPrincipal EmployeePrincipal principal) {
        
        Long cashierId = principal != null ? principal.getEmployee().getId() : null;
        InvoiceDto paid = invoiceService.payInvoice(id, cashierId);
        return ResponseEntity.ok(ApiResponse.success(paid, "Invoice marked as paid and stock levels updated successfully"));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InvoiceDto>> cancelInvoice(@PathVariable Long id) {
        InvoiceDto cancelled = invoiceService.cancelInvoice(id);
        return ResponseEntity.ok(ApiResponse.success(cancelled, "Invoice cancelled successfully"));
    }
}
