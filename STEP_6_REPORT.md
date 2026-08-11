# BÁO CÁO TIẾN ĐỘ - BƯỚC 6: MODULE KHO HÀNG, POS & HÓA ĐƠN (INVENTORY, POS & BILLING)

Chúng tôi đã hoàn thành **Bước 6** theo lộ trình phát triển hệ thống quản lý Spa. Dưới đây là báo cáo chi tiết về tiến độ và các logic nghiệp vụ hoá đơn, POS & trừ kho tự động đã được triển khai trong từng file ở máy local của bạn.

*(Lưu ý: Theo yêu cầu của bạn, các thay đổi này mới chỉ lưu ở máy local và CHƯA được push lên Git để bạn kiểm tra trước khi tạo 1 commit gộp).*

---

## 🛠️ CÁC LOGIC VÀ PHẦN MỀM ĐÃ TRIỂN KHAI

### 1. Tương tác Cơ sở dữ liệu (Repository Layer)
*   **[InvoiceRepository.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/repository/InvoiceRepository.java)**:
    *   *Nhiệm vụ*: Tương tác bảng `invoices`.
    *   *Logic*: Tra cứu hóa đơn theo khách hàng (`findByCustomerId`), theo lịch hẹn (`findByAppointmentId`) và theo mã hóa đơn (`findByInvoiceNo`).
*   **[InvoiceItemRepository.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/repository/InvoiceItemRepository.java)**:
    *   *Nhiệm vụ*: Tương tác bảng `invoice_items` (các dòng chi tiết trong hóa đơn).
*   **[ProductRepository.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/repository/ProductRepository.java)**:
    *   *Nhiệm vụ*: Tương tác bảng `products` (sản phẩm kho hàng).
    *   *Logic*: Tra cứu sản phẩm bằng mã `sku`.
*   **[StockTransactionRepository.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/repository/StockTransactionRepository.java)**:
    *   *Nhiệm vụ*: Tương tác bảng `stock_transactions` (Nhật ký giao dịch xuất/nhập kho).

---

### 2. Các lớp truyền tải dữ liệu (DTO Layer)
*   **[InvoiceDto.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/dto/InvoiceDto.java)**: Chuyển tải thông tin hóa đơn (Tổng tiền, Giảm giá thành viên, Điểm tích lũy nhận được/sử dụng, Trạng thái thanh toán và danh sách chi tiết các mặt hàng dịch vụ/sản phẩm đã sử dụng).
*   **[InvoiceItemDto.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/dto/InvoiceItemDto.java)**: Chi tiết mặt hàng (Loại mặt hàng là Dịch vụ hoặc Sản phẩm bán lẻ, Đơn giá, Số lượng, Thành tiền).
*   **[InvoiceCreateRequestDto.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/dto/InvoiceCreateRequestDto.java)**: Chứa thông tin tạo hóa đơn (ID lịch hẹn, điểm muốn tiêu dùng, danh sách sản phẩm bán lẻ mua thêm kèm số lượng).

---

### 3. Nghiệp vụ thanh toán POS & Khấu trừ kho (Service Layer)
*   **[InvoiceServiceImpl.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/service/InvoiceServiceImpl.java)**:
    *   *createInvoiceFromAppointment*: 
        1. Tự động đọc danh sách dịch vụ con từ lịch hẹn chuyển thành các dòng dịch vụ trong hoá đơn.
        2. Tự động thêm sản phẩm bán lẻ mua thêm theo yêu cầu.
        3. Áp dụng chính sách giảm giá dựa trên hạng thành viên của khách hàng (`MembershipTier.discountPercent`).
        4. Hỗ trợ tiêu điểm tích luỹ khách hàng (khấu trừ tiền trực tiếp: 1 điểm = 1,000 VND).
        5. Tích điểm tự động khi thanh toán: Cộng 1 điểm cho mỗi 100,000 VND trên hóa đơn cuối cùng.
    *   *payInvoice* (Thanh toán & POS):
        1. Chuyển hóa đơn sang trạng thái `PAID`.
        2. Tự động cộng/trừ điểm tích luỹ thực tế vào hồ sơ khách hàng thông qua `CustomerService` và tự động cập nhật nâng/hạ hạng thẻ thành viên nếu đạt hạn mức mới.
        3. Cập nhật trạng thái lịch hẹn tương ứng thành `COMPLETED` và hoàn thành đồng loạt dịch vụ đi kèm thành `DONE`.
        4. Tự động trừ số lượng tồn kho (`currentStock`) trực tiếp của các sản phẩm bán lẻ trong hoá đơn và tạo nhật ký xuất kho `StockTransaction` với phân loại `RETAIL_SALE`.
*   **[InventoryServiceImpl.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/service/InventoryServiceImpl.java)**:
    *   Cung cấp các hàm quản lý tồn kho thủ công như nhập/xuất và điều chỉnh số lượng tồn kho trực tiếp (`adjustStock`) đồng thời ghi log giao dịch kho đầy đủ.

---

### 4. Giao diện REST API (Controller Layer)
*   **[InvoiceController.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/controller/InvoiceController.java)**:
    *   Đường dẫn: `/api/invoices`.
    *   *Endpoints*:
        *   `POST /api/invoices`: Khởi tạo hóa đơn nháp/chưa thanh toán từ Lịch hẹn (bao gồm cả sản phẩm bán lẻ mua thêm).
        *   `PUT /api/invoices/{id}/pay`: Thanh toán hóa đơn (chuyển trạng thái hóa đơn, hoàn tất lịch hẹn, tích điểm khách hàng và khấu trừ tồn kho sản phẩm tự động).
        *   `PUT /api/invoices/{id}/cancel`: Hủy hóa đơn (yêu cầu quyền Admin).

---

## 📈 TIẾN TRÌNH THỰC HIỆN KẾ HOẠCH
- [x] Tạo các Repositories quản lý Hóa đơn & Kho hàng
- [x] Thiết lập cấu trúc dữ liệu Invoice DTOs nhận/phản hồi thông tin
- [x] Triển khai tính toán giảm giá theo hạng thẻ & Tiêu dùng điểm tích lũy
- [x] Triển khai tự động tích lũy điểm thưởng & Thay đổi hạng thẻ khách hàng khi thanh toán
- [x] Triển khai tự động trừ tồn kho sản phẩm & Ghi log StockTransaction
- [x] Biên dịch kiểm thử thành công (`BUILD SUCCESS`)
