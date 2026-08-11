package com.example.demodevops.dto;

import java.util.List;

public class InvoiceCreateRequestDto {
    private Long appointmentId;
    private Integer pointsUsed = 0;
    private String note;
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
        private Long productId;
        private Integer quantity;

        public ProductPurchaseDto() {}

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
