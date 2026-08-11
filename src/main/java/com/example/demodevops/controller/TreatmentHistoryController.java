package com.example.demodevops.controller;

import com.example.demodevops.dto.ApiResponse;
import com.example.demodevops.dto.TreatmentHistoryDto;
import com.example.demodevops.service.TreatmentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treatment-histories")
public class TreatmentHistoryController {

    private final TreatmentHistoryService historyService;

    @Autowired
    public TreatmentHistoryController(TreatmentHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<List<TreatmentHistoryDto>>> getHistoryByCustomer(@PathVariable Long customerId) {
        List<TreatmentHistoryDto> history = historyService.getHistoryByCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success(history, "Retrieved customer treatment records successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<TreatmentHistoryDto>> getHistoryById(@PathVariable Long id) {
        TreatmentHistoryDto record = historyService.getHistoryById(id);
        return ResponseEntity.ok(ApiResponse.success(record, "Retrieved treatment record successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<TreatmentHistoryDto>> addHistoryRecord(@RequestBody TreatmentHistoryDto dto) {
        TreatmentHistoryDto created = historyService.addHistoryRecord(dto);
        return new ResponseEntity<>(ApiResponse.success(created, "Treatment record added successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'TECHNICIAN')")
    public ResponseEntity<ApiResponse<TreatmentHistoryDto>> updateHistoryRecord(
            @PathVariable Long id, 
            @RequestBody TreatmentHistoryDto dto) {
        TreatmentHistoryDto updated = historyService.updateHistoryRecord(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Treatment record updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteHistoryRecord(@PathVariable Long id) {
        historyService.deleteHistoryRecord(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Treatment record deleted successfully"));
    }
}
