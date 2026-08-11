package com.example.demodevops.service;

import com.example.demodevops.dto.TreatmentHistoryDto;
import java.util.List;

public interface TreatmentHistoryService {
    List<TreatmentHistoryDto> getHistoryByCustomer(Long customerId);
    TreatmentHistoryDto getHistoryById(Long id);
    TreatmentHistoryDto addHistoryRecord(TreatmentHistoryDto historyDto);
    TreatmentHistoryDto updateHistoryRecord(Long id, TreatmentHistoryDto historyDto);
    void deleteHistoryRecord(Long id);
}
