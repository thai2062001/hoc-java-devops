package com.example.demodevops.service;

import com.example.demodevops.dto.CustomerDto;
import com.example.demodevops.dto.CustomerSaveDto;
import java.util.List;

public interface CustomerService {
    List<CustomerDto> getAllCustomers();
    CustomerDto getCustomerById(Long id);
    CustomerDto getCustomerByPhone(String phone);
    CustomerDto createCustomer(CustomerSaveDto customerSaveDto);
    CustomerDto updateCustomer(Long id, CustomerSaveDto customerSaveDto);
    CustomerDto updateLoyaltyPoints(Long id, Integer pointsChange);
    void deleteCustomer(Long id);
}
