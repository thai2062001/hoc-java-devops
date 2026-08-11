package com.example.demodevops.service;

import com.example.demodevops.dto.MembershipTierDto;
import java.util.List;

public interface MembershipTierService {
    List<MembershipTierDto> getAllTiers();
    MembershipTierDto getTierById(Long id);
    MembershipTierDto createTier(MembershipTierDto tierDto);
    MembershipTierDto updateTier(Long id, MembershipTierDto tierDto);
    void deleteTier(Long id);
}
