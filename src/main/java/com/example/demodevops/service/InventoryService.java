package com.example.demodevops.service;

import com.example.demodevops.model.Product;
import java.math.BigDecimal;
import java.util.List;

public interface InventoryService {
    List<Product> getAllInventory();
    Product adjustStock(Long productId, BigDecimal quantity, String transactionType, String note, Long employeeId);
}
