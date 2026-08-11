package com.example.demodevops.service;

import com.example.demodevops.dto.CustomerDto;
import com.example.demodevops.dto.CustomerSaveDto;
import com.example.demodevops.exception.ResourceNotFoundException;
import com.example.demodevops.model.Customer;
import com.example.demodevops.model.MembershipTier;
import com.example.demodevops.repository.CustomerRepository;
import com.example.demodevops.repository.MembershipTierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final MembershipTierRepository tierRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, MembershipTierRepository tierRepository) {
        this.customerRepository = customerRepository;
        this.tierRepository = tierRepository;
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return convertToDto(customer);
    }

    @Override
    public CustomerDto getCustomerByPhone(String phone) {
        Customer customer = customerRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with phone: " + phone));
        return convertToDto(customer);
    }

    @Override
    public CustomerDto createCustomer(CustomerSaveDto dto) {
        Customer customer = new Customer();
        customer.setCustomerCode(dto.getCustomerCode());
        customer.setFullName(dto.getFullName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setDob(dto.getDob());
        customer.setGender(dto.getGender());
        customer.setAddress(dto.getAddress());
        customer.setAvatarUrl(dto.getAvatarUrl());
        customer.setSkinType(dto.getSkinType());
        customer.setMedicalNotes(dto.getMedicalNotes());
        customer.setSource(dto.getSource());
        customer.setStatus(dto.getStatus() != null ? dto.getStatus() : Customer.CustomerStatus.ACTIVE);
        customer.setLoyaltyPoints(0);

        // Gán hạng thẻ dựa trên điểm tích luỹ ban đầu (0 điểm)
        assignTierBasedOnPoints(customer);

        Customer saved = customerRepository.save(customer);
        return convertToDto(saved);
    }

    @Override
    public CustomerDto updateCustomer(Long id, CustomerSaveDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setCustomerCode(dto.getCustomerCode());
        customer.setFullName(dto.getFullName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setDob(dto.getDob());
        customer.setGender(dto.getGender());
        customer.setAddress(dto.getAddress());
        customer.setAvatarUrl(dto.getAvatarUrl());
        customer.setSkinType(dto.getSkinType());
        customer.setMedicalNotes(dto.getMedicalNotes());
        customer.setSource(dto.getSource());
        if (dto.getStatus() != null) {
            customer.setStatus(dto.getStatus());
        }

        Customer updated = customerRepository.save(customer);
        return convertToDto(updated);
    }

    @Override
    public CustomerDto updateLoyaltyPoints(Long id, Integer pointsChange) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        int newPoints = Math.max(0, customer.getLoyaltyPoints() + pointsChange);
        customer.setLoyaltyPoints(newPoints);

        // Cập nhật lại hạng thẻ thành viên dựa trên điểm mới
        assignTierBasedOnPoints(customer);

        Customer updated = customerRepository.save(customer);
        return convertToDto(updated);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
    }

    private void assignTierBasedOnPoints(Customer customer) {
        List<MembershipTier> tiers = tierRepository.findAll().stream()
                .sorted(Comparator.comparing(MembershipTier::getMinPoints).reversed())
                .collect(Collectors.toList());

        for (MembershipTier tier : tiers) {
            if (customer.getLoyaltyPoints() >= tier.getMinPoints()) {
                customer.setMembershipTier(tier);
                return;
            }
        }
        customer.setMembershipTier(null);
    }

    private CustomerDto convertToDto(Customer c) {
        return new CustomerDto(
                c.getId(),
                c.getMembershipTier() != null ? c.getMembershipTier().getId() : null,
                c.getMembershipTier() != null ? c.getMembershipTier().getName() : "Không có hạng",
                c.getCustomerCode(),
                c.getFullName(),
                c.getPhone(),
                c.getEmail(),
                c.getDob(),
                c.getGender(),
                c.getAddress(),
                c.getAvatarUrl(),
                c.getLoyaltyPoints(),
                c.getSkinType(),
                c.getMedicalNotes(),
                c.getSource(),
                c.getStatus(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
