package com.example.demodevops.service;

import com.example.demodevops.dto.ShiftDto;
import com.example.demodevops.exception.ResourceNotFoundException;
import com.example.demodevops.model.Shift;
import com.example.demodevops.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;

    @Autowired
    public ShiftServiceImpl(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Override
    public List<ShiftDto> getAllShifts() {
        return shiftRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShiftDto getShiftById(Long id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + id));
        return convertToDto(shift);
    }

    @Override
    public ShiftDto createShift(ShiftDto shiftDto) {
        Shift shift = new Shift();
        shift.setName(shiftDto.getName());
        shift.setStartTime(shiftDto.getStartTime());
        shift.setEndTime(shiftDto.getEndTime());
        Shift saved = shiftRepository.save(shift);
        return convertToDto(saved);
    }

    @Override
    public ShiftDto updateShift(Long id, ShiftDto shiftDto) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + id));
        shift.setName(shiftDto.getName());
        shift.setStartTime(shiftDto.getStartTime());
        shift.setEndTime(shiftDto.getEndTime());
        Shift updated = shiftRepository.save(shift);
        return convertToDto(updated);
    }

    @Override
    public void deleteShift(Long id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + id));
        shiftRepository.delete(shift);
    }

    private ShiftDto convertToDto(Shift shift) {
        return new ShiftDto(
                shift.getId(),
                shift.getName(),
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getCreatedAt(),
                shift.getUpdatedAt()
        );
    }
}
