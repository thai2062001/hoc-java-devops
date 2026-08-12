package com.example.demodevops.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class InvoiceCreateRequestDto {
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;
    private Integer pointsUsed = 0;
    private String note;

    @Valid
    private List<ProductPurchaseDto> productPurchases;

    public InvoiceCreateRequestDto() {}

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public Integer getPointsUsed() { return pointsUsed; }
    public void setPointsUsed(Integer pointsUsed) { this.pointsUsed = pointsUsed; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public List<ProductPurchaseDto> getProductPurchases() { return productPurchases; }
    public void setProductPurchases(List<ProductPurchaseDto> productPurchases) { this.productPurchases = productPurchases; }

    public static class ProductPurchaseDto {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        public ProductPurchaseDto() {}

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
