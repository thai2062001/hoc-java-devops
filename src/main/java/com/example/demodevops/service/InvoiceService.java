package com.example.demodevops.service;

import com.example.demodevops.dto.InvoiceCreateRequestDto;
import com.example.demodevops.dto.InvoiceDto;
import com.example.demodevops.model.Invoice.InvoiceStatus;

import java.util.List;

public interface InvoiceService {
    List<InvoiceDto> getAllInvoices();
    InvoiceDto getInvoiceById(Long id);
    List<InvoiceDto> getInvoicesByCustomer(Long customerId);
    InvoiceDto createInvoiceFromAppointment(InvoiceCreateRequestDto request, Long cashierId);
    InvoiceDto payInvoice(Long id, Long cashierId);
    InvoiceDto cancelInvoice(Long id);
}
