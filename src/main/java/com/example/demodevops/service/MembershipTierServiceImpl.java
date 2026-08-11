package com.example.demodevops.service;

import com.example.demodevops.dto.MembershipTierDto;
import com.example.demodevops.exception.ResourceNotFoundException;
import com.example.demodevops.model.MembershipTier;
import com.example.demodevops.repository.MembershipTierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MembershipTierServiceImpl implements MembershipTierService {

    private final MembershipTierRepository tierRepository;

    @Autowired
    public MembershipTierServiceImpl(MembershipTierRepository tierRepository) {
        this.tierRepository = tierRepository;
    }

    @Override
    public List<MembershipTierDto> getAllTiers() {
        return tierRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MembershipTierDto getTierById(Long id) {
        MembershipTier tier = tierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership Tier not found with id: " + id));
        return convertToDto(tier);
    }

    @Override
    public MembershipTierDto createTier(MembershipTierDto tierDto) {
        MembershipTier tier = new MembershipTier();
        tier.setCode(tierDto.getCode());
        tier.setName(tierDto.getName());
        tier.setMinPoints(tierDto.getMinPoints());
        tier.setDiscountPercent(tierDto.getDiscountPercent());
        MembershipTier saved = tierRepository.save(tier);
        return convertToDto(saved);
    }

    @Override
    public MembershipTierDto updateTier(Long id, MembershipTierDto tierDto) {
        MembershipTier tier = tierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership Tier not found with id: " + id));
        tier.setCode(tierDto.getCode());
        tier.setName(tierDto.getName());
        tier.setMinPoints(tierDto.getMinPoints());
        tier.setDiscountPercent(tierDto.getDiscountPercent());
        MembershipTier updated = tierRepository.save(tier);
        return convertToDto(updated);
    }

    @Override
    public void deleteTier(Long id) {
        MembershipTier tier = tierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership Tier not found with id: " + id));
        tierRepository.delete(tier);
    }

    private MembershipTierDto convertToDto(MembershipTier tier) {
        return new MembershipTierDto(
                tier.getId(),
                tier.getCode(),
                tier.getName(),
                tier.getMinPoints(),
                tier.getDiscountPercent(),
                tier.getCreatedAt(),
                tier.getUpdatedAt()
        );
    }
}
