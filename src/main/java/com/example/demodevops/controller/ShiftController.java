package com.example.demodevops.controller;

import com.example.demodevops.dto.ApiResponse;
import com.example.demodevops.dto.ShiftDto;
import com.example.demodevops.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    private final ShiftService shiftService;

    @Autowired
    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<ShiftDto>>> getAllShifts() {
        List<ShiftDto> shifts = shiftService.getAllShifts();
        return ResponseEntity.ok(ApiResponse.success(shifts, "Retrieved all shifts successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<ShiftDto>> getShiftById(@PathVariable Long id) {
        ShiftDto shift = shiftService.getShiftById(id);
        return ResponseEntity.ok(ApiResponse.success(shift, "Retrieved shift successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShiftDto>> createShift(@RequestBody ShiftDto shiftDto) {
        ShiftDto created = shiftService.createShift(shiftDto);
        return new ResponseEntity<>(ApiResponse.success(created, "Shift created successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShiftDto>> updateShift(@PathVariable Long id, @RequestBody ShiftDto shiftDto) {
        ShiftDto updated = shiftService.updateShift(id, shiftDto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Shift updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteShift(@PathVariable Long id) {
        shiftService.deleteShift(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Shift deleted successfully"));
    }
}
