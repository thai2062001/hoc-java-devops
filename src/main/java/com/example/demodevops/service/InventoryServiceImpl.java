package com.example.demodevops.service;

import com.example.demodevops.exception.ResourceNotFoundException;
import com.example.demodevops.model.Employee;
import com.example.demodevops.model.Product;
import com.example.demodevops.model.StockTransaction;
import com.example.demodevops.model.StockTransaction.ReferenceType;
import com.example.demodevops.model.StockTransaction.TransactionType;
import com.example.demodevops.repository.EmployeeRepository;
import com.example.demodevops.repository.ProductRepository;
import com.example.demodevops.repository.StockTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final StockTransactionRepository stockTransactionRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public InventoryServiceImpl(ProductRepository productRepository,
                                StockTransactionRepository stockTransactionRepository,
                                EmployeeRepository employeeRepository) {
        this.productRepository = productRepository;
        this.stockTransactionRepository = stockTransactionRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Product> getAllInventory() {
        return productRepository.findAll();
    }

    @Override
    public Product adjustStock(Long productId, BigDecimal quantity, String transactionType, String note, Long employeeId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Employee employee = employeeId != null ? employeeRepository.findById(employeeId).orElse(null) : null;
        TransactionType type = TransactionType.valueOf(transactionType.toUpperCase());

        BigDecimal currentStock = product.getCurrentStock() != null ? product.getCurrentStock() : BigDecimal.ZERO;
        if (type == TransactionType.IMPORT) {
            product.setCurrentStock(currentStock.add(quantity));
        } else if (type == TransactionType.EXPORT || type == TransactionType.ADJUSTMENT) {
            product.setCurrentStock(currentStock.subtract(quantity));
        }

        Product savedProduct = productRepository.save(product);

        // Ghi nhật ký StockTransaction
        StockTransaction tx = new StockTransaction();
        tx.setProduct(savedProduct);
        tx.setEmployee(employee);
        tx.setType(type);
        tx.setReferenceType(ReferenceType.ADJUSTMENT);
        tx.setQuantity(quantity);
        tx.setNote(note);
        tx.setTransactionDate(LocalDateTime.now());
        stockTransactionRepository.save(tx);

        return savedProduct;
    }
}
