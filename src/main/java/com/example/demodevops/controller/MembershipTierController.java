package com.example.demodevops.controller;

import com.example.demodevops.dto.ApiResponse;
import com.example.demodevops.dto.MembershipTierDto;
import com.example.demodevops.service.MembershipTierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership-tiers")
public class MembershipTierController {

    private final MembershipTierService tierService;

    @Autowired
    public MembershipTierController(MembershipTierService tierService) {
        this.tierService = tierService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<MembershipTierDto>>> getAllTiers() {
        List<MembershipTierDto> tiers = tierService.getAllTiers();
        return ResponseEntity.ok(ApiResponse.success(tiers, "Retrieved all tiers successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<MembershipTierDto>> getTierById(@PathVariable Long id) {
        MembershipTierDto tier = tierService.getTierById(id);
        return ResponseEntity.ok(ApiResponse.success(tier, "Retrieved tier successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MembershipTierDto>> createTier(@RequestBody MembershipTierDto tierDto) {
        MembershipTierDto created = tierService.createTier(tierDto);
        return new ResponseEntity<>(ApiResponse.success(created, "Tier created successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MembershipTierDto>> updateTier(@PathVariable Long id, @RequestBody MembershipTierDto tierDto) {
        MembershipTierDto updated = tierService.updateTier(id, tierDto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Tier updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTier(@PathVariable Long id) {
        tierService.deleteTier(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Tier deleted successfully"));
    }
}
