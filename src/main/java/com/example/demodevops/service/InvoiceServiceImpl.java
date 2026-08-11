package com.example.demodevops.service;

import com.example.demodevops.dto.InvoiceCreateRequestDto;
import com.example.demodevops.dto.InvoiceDto;
import com.example.demodevops.dto.InvoiceItemDto;
import com.example.demodevops.exception.ResourceNotFoundException;
import com.example.demodevops.model.*;
import com.example.demodevops.model.Invoice.InvoiceStatus;
import com.example.demodevops.model.InvoiceItem.ItemType;
import com.example.demodevops.model.StockTransaction.ReferenceType;
import com.example.demodevops.model.StockTransaction.TransactionType;
import com.example.demodevops.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final ProductRepository productRepository;
    private final StockTransactionRepository stockTransactionRepository;
    private final CommissionRepository commissionRepository;
    private final ServiceCommissionRateRepository serviceCommissionRateRepository;
    private final CustomerService customerService;

    @Autowired
    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              InvoiceItemRepository invoiceItemRepository,
                              AppointmentRepository appointmentRepository,
                              AppointmentServiceRepository appointmentServiceRepository,
                              CustomerRepository customerRepository,
                              EmployeeRepository employeeRepository,
                              ProductRepository productRepository,
                              StockTransactionRepository stockTransactionRepository,
                              CommissionRepository commissionRepository,
                              ServiceCommissionRateRepository serviceCommissionRateRepository,
                              CustomerService customerService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentServiceRepository = appointmentServiceRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.productRepository = productRepository;
        this.stockTransactionRepository = stockTransactionRepository;
        this.commissionRepository = commissionRepository;
        this.serviceCommissionRateRepository = serviceCommissionRateRepository;
        this.customerService = customerService;
    }

    @Override
    public List<InvoiceDto> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDto getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return convertToDto(invoice);
    }

    @Override
    public List<InvoiceDto> getInvoicesByCustomer(Long customerId) {
        return invoiceRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDto createInvoiceFromAppointment(InvoiceCreateRequestDto request, Long cashierId) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + request.getAppointmentId()));

        // Kiểm tra xem lịch hẹn đã được xuất hóa đơn chưa
        Optional<Invoice> existing = invoiceRepository.findByAppointmentId(appointment.getId());
        if (existing.isPresent()) {
            return convertToDto(existing.get());
        }

        Customer customer = appointment.getCustomer();
        Employee cashier = cashierId != null ? employeeRepository.findById(cashierId).orElse(null) : null;

        Invoice invoice = new Invoice();
        invoice.setInvoiceCode("INV-" + System.currentTimeMillis());
        invoice.setCustomer(customer);
        invoice.setAppointment(appointment);
        invoice.setCashier(cashier);
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setNote(request.getNote());

        BigDecimal subtotal = BigDecimal.ZERO;
        List<InvoiceItem> items = new ArrayList<>();

        // 1. Phân tích dịch vụ từ Appointment
        List<com.example.demodevops.model.AppointmentService> appointmentServices = 
                appointmentServiceRepository.findByAppointmentId(appointment.getId());

        for (com.example.demodevops.model.AppointmentService appSvc : appointmentServices) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setItemType(ItemType.SERVICE);
            item.setService(appSvc.getService());
            item.setQuantity(appSvc.getQuantity());
            item.setUnitPrice(appSvc.getUnitPrice());
            
            BigDecimal itemSubtotal = appSvc.getUnitPrice().multiply(BigDecimal.valueOf(appSvc.getQuantity()));
            item.setDiscountAmount(BigDecimal.ZERO);
            item.setTotalPrice(itemSubtotal);
            items.add(item);

            subtotal = subtotal.add(itemSubtotal);
        }

        // 2. Phân tích sản phẩm bán lẻ mua kèm (nếu có)
        if (request.getProductPurchases() != null) {
            for (InvoiceCreateRequestDto.ProductPurchaseDto prodDto : request.getProductPurchases()) {
                Product product = productRepository.findById(prodDto.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + prodDto.getProductId()));

                InvoiceItem item = new InvoiceItem();
                item.setInvoice(invoice);
                item.setItemType(ItemType.PRODUCT);
                item.setProduct(product);
                item.setQuantity(prodDto.getQuantity());
                item.setUnitPrice(product.getRetailPrice() != null ? product.getRetailPrice() : BigDecimal.ZERO);
                
                BigDecimal itemSubtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(prodDto.getQuantity()));
                item.setDiscountAmount(BigDecimal.ZERO);
                item.setTotalPrice(itemSubtotal);
                items.add(item);

                subtotal = subtotal.add(itemSubtotal);
            }
        }

        // 3. Tính toán giảm giá theo hạng thành viên
        BigDecimal discountPercent = BigDecimal.ZERO;
        if (customer.getMembershipTier() != null && customer.getMembershipTier().getDiscountPercent() != null) {
            discountPercent = customer.getMembershipTier().getDiscountPercent();
        }
        
        BigDecimal discountAmount = subtotal.multiply(discountPercent).divide(BigDecimal.valueOf(100));
        
        // 4. Khấu trừ điểm tích luỹ (1 điểm = 1,000 VND)
        BigDecimal pointsUsedValue = BigDecimal.valueOf(request.getPointsUsed()).multiply(BigDecimal.valueOf(1000));
        if (pointsUsedValue.compareTo(subtotal.subtract(discountAmount)) > 0) {
            pointsUsedValue = subtotal.subtract(discountAmount);
        }
        
        discountAmount = discountAmount.add(pointsUsedValue);

        BigDecimal totalAmount = subtotal.subtract(discountAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        // 5. Tích điểm (100,000 VND = 1 điểm)
        int pointsEarned = totalAmount.divide(BigDecimal.valueOf(100000)).intValue();

        invoice.setSubtotalAmount(subtotal);
        invoice.setDiscountAmount(discountAmount);
        invoice.setTaxAmount(BigDecimal.ZERO);
        invoice.setTotalAmount(totalAmount);
        invoice.setPointsEarned(pointsEarned);
        invoice.setPointsUsed(request.getPointsUsed());

        Invoice saved = invoiceRepository.save(invoice);
        for (InvoiceItem item : items) {
            invoiceItemRepository.save(item);
        }

        return convertToDto(saved, items);
    }

    @Override
    public InvoiceDto payInvoice(Long id, Long cashierId) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            return convertToDto(invoice);
        }

        Employee cashier = cashierId != null ? employeeRepository.findById(cashierId).orElse(null) : null;
        if (cashier != null) {
            invoice.setCashier(cashier);
        }

        invoice.setStatus(InvoiceStatus.PAID);
        Invoice saved = invoiceRepository.save(invoice);

        // 1. Cập nhật điểm tích luỹ khách hàng & Tự động nâng/hạ hạng thành viên
        Customer customer = invoice.getCustomer();
        int pointsChange = invoice.getPointsEarned() - invoice.getPointsUsed();
        customerService.updateLoyaltyPoints(customer.getId(), pointsChange);

        // 2. Cập nhật trạng thái lịch hẹn tương ứng thành COMPLETED & Tính hoa hồng kỹ thuật viên
        if (invoice.getAppointment() != null) {
            Appointment appointment = invoice.getAppointment();
            appointment.setStatus(com.example.demodevops.model.Appointment.AppointmentStatus.COMPLETED);
            appointmentRepository.save(appointment);

            // Hoàn tất toàn bộ dịch vụ đặt kèm & tính hoa hồng cho từng Kỹ thuật viên phụ trách
            List<com.example.demodevops.model.AppointmentService> details = 
                    appointmentServiceRepository.findByAppointmentId(appointment.getId());
            for (com.example.demodevops.model.AppointmentService detail : details) {
                detail.setStatus(com.example.demodevops.model.AppointmentService.AppointmentServiceStatus.DONE);
                appointmentServiceRepository.save(detail);

                // Tính toán hoa hồng cho nhân viên thực hiện dịch vụ
                if (detail.getEmployee() != null) {
                    Employee tech = detail.getEmployee();
                    BigDecimal rate = BigDecimal.valueOf(10); // Mức hoa hồng mặc định 10% nếu không có cấu hình riêng
                    
                    Optional<ServiceCommissionRate> rateOpt = serviceCommissionRateRepository
                            .findByServiceIdAndEmployeeId(detail.getService().getId(), tech.getId());
                    if (rateOpt.isPresent()) {
                        rate = rateOpt.get().getCommissionPercent();
                    }

                    BigDecimal commissionAmount = detail.getUnitPrice()
                            .multiply(BigDecimal.valueOf(detail.getQuantity()))
                            .multiply(rate)
                            .divide(BigDecimal.valueOf(100));

                    Commission commission = new Commission();
                    commission.setAppointmentService(detail);
                    commission.setEmployee(tech);
                    commission.setInvoiceId(invoice.getId());
                    commission.setRateApplied(rate);
                    commission.setAmount(commissionAmount);
                    commission.setStatus(Commission.CommissionStatus.PENDING);
                    commissionRepository.save(commission);
                }
            }
        }

        // 3. Trừ hàng tồn kho và tạo nhật ký giao dịch kho cho các sản phẩm trong hóa đơn
        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(invoice.getId());
        for (InvoiceItem item : items) {
            if (item.getItemType() == ItemType.PRODUCT && item.getProduct() != null) {
                Product product = item.getProduct();
                BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                
                // Trừ tồn kho
                product.setCurrentStock(product.getCurrentStock().subtract(quantity));
                productRepository.save(product);

                // Ghi nhật ký StockTransaction
                StockTransaction tx = new StockTransaction();
                tx.setProduct(product);
                tx.setEmployee(cashier);
                tx.setType(TransactionType.EXPORT);
                tx.setReferenceType(ReferenceType.RETAIL_SALE);
                tx.setReferenceId(invoice.getId());
                tx.setQuantity(quantity);
                tx.setNote("Xuất bán lẻ theo hoá đơn " + invoice.getInvoiceCode());
                tx.setTransactionDate(LocalDateTime.now());
                stockTransactionRepository.save(tx);
            }
        }

        return convertToDto(saved, items);
    }

    @Override
    public InvoiceDto cancelInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        invoice.setStatus(InvoiceStatus.CANCELLED);
        Invoice saved = invoiceRepository.save(invoice);
        return convertToDto(saved);
    }

    private InvoiceDto convertToDto(Invoice invoice) {
        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(invoice.getId());
        return convertToDto(invoice, items);
    }

    private InvoiceDto convertToDto(Invoice invoice, List<InvoiceItem> items) {
        List<InvoiceItemDto> itemDtos = items.stream()
                .map(i -> new InvoiceItemDto(
                        i.getId(),
                        i.getInvoice().getId(),
                        i.getItemType(),
                        i.getItemType() == ItemType.SERVICE ? i.getService().getId() : i.getProduct().getId(),
                        i.getItemType() == ItemType.SERVICE ? i.getService().getName() : i.getProduct().getName(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getDiscountAmount(),
                        i.getTotalPrice(),
                        i.getCreatedAt(),
                        i.getUpdatedAt()
                )).collect(Collectors.toList());

        return new InvoiceDto(
                invoice.getId(),
                invoice.getInvoiceCode(),
                invoice.getCustomer().getId(),
                invoice.getCustomer().getFullName(),
                invoice.getAppointment() != null ? invoice.getAppointment().getId() : null,
                invoice.getCashier() != null ? invoice.getCashier().getId() : null,
                invoice.getCashier() != null ? invoice.getCashier().getFullName() : "Chưa thanh toán",
                invoice.getSubtotalAmount(),
                invoice.getDiscountAmount(),
                invoice.getTaxAmount(),
                invoice.getTotalAmount(),
                invoice.getPointsEarned(),
                invoice.getPointsUsed(),
                invoice.getStatus(),
                invoice.getNote(),
                itemDtos,
                invoice.getCreatedAt(),
                invoice.getUpdatedAt()
        );
    }
}
