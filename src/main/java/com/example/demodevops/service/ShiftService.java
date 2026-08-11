package com.example.demodevops.service;

import com.example.demodevops.dto.ShiftDto;
import java.util.List;

public interface ShiftService {
    List<ShiftDto> getAllShifts();
    ShiftDto getShiftById(Long id);
    ShiftDto createShift(ShiftDto shiftDto);
    ShiftDto updateShift(Long id, ShiftDto shiftDto);
    void deleteShift(Long id);
}
