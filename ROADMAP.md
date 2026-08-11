# LỘ TRÌNH PHÁT TRIỂN HỆ THỐNG QUẢN LÝ SPA
*(Java Spring Boot + Next.js + MySQL)*

Tài liệu này lưu trữ lộ trình phát triển chi tiết của dự án, được chia thành các bước rõ ràng từ nền tảng đến các nghiệp vụ phức tạp.

---

## 📋 TRẠNG THÁI HIỆN TẠI (CURRENT STATE)
- [x] Thiết kế Database Schema hoàn chỉnh (29 bảng)
- [x] Tạo file `init.sql` và tích hợp cơ chế tự động tạo bảng vào Docker Compose
- [x] Đồng bộ Database MySQL trong `docker-compose.yml` và `application.properties`
- [x] Tạo toàn bộ 29 JPA Java Entities tương ứng với database
- [x] Build thành công dự án trên môi trường CI/CD (GitHub Actions)

---

## 🚀 LỘ TRÌNH CHI TIẾT (ROADMAP)

### 📌 BƯỚC 1: Dựng khung API chuẩn & CRUD Dịch vụ Spa (Services & Categories)
*Mục tiêu: Đảm bảo luồng xử lý dữ liệu Entity → Repository → Service → DTO → Controller hoạt động trơn tru.*
- [x] Dựng khung phản hồi chuẩn toàn cục (Global Response Wrapper: `ApiResponse`).
- [x] Xử lý ngoại lệ toàn cục (Global Exception Handling & `@ControllerAdvice`).
- [x] Viết CRUD cho **ServiceCategory** (Danh mục dịch vụ) và **Service** (Dịch vụ Spa).
- [x] Viết API test thử nghiệm để lễ tân/khách xem danh sách dịch vụ.

### 📌 BƯỚC 2: Bảo mật & Xác thực với JWT (Spring Security)
*Mục tiêu: Thiết lập hệ thống phân quyền trước khi viết thêm nhiều API khác.*
- [x] Tích hợp Spring Security & thư viện JWT.
- [x] Cấu hình UserDetailsService sử dụng bảng `employees` làm tài khoản đăng nhập.
- [x] Xây dựng luồng Đăng nhập (Login API) cấp phát JWT Token.
- [x] Phân quyền các endpoint dựa trên vai trò nhân viên (Admin, Lễ tân, Kỹ thuật viên).

### 📌 BƯỚC 3: Module Nhân viên & Ca làm việc (Employees, Roles, Shifts)
*Mục tiêu: Quản lý nguồn nhân lực của Spa và phục vụ chức năng đăng nhập/phân quyền.*
- [x] Viết CRUD quản lý danh sách Nhân viên (`employees`).
- [x] Quản lý Ca làm việc (`shifts`) và lịch trực của nhân viên (`employee_shifts`).
- [x] API lấy danh sách Kỹ thuật viên đang rảnh trong ngày phục vụ đặt lịch.

### 📌 BƯỚC 4: Module Khách hàng & Thẻ thành viên (CRM)
*Mục tiêu: Quản lý thông tin khách hàng, hạng thẻ và lịch sử trị liệu.*
- [x] Viết CRUD quản lý thông tin Khách hàng (`customers`).
- [x] Quản lý hạng thành viên (`membership_tiers`) và điểm tích lũy.
- [x] Lưu trữ và cập nhật lịch sử trị liệu (`treatment_history`) của khách.

### 📌 BƯỚC 5: Module Đặt lịch (Booking)
*Mục tiêu: Nghiệp vụ cốt lõi và phức tạp nhất của Spa.*
- [x] API Đặt lịch hẹn (`appointments`) trực tuyến/tại quầy.
- [x] Thuật toán kiểm tra xung đột lịch (trùng giường, trùng kỹ thuật viên).
- [x] Gán nhiều dịch vụ (`appointment_services`) và kỹ thuật viên cho từng ca.

### 📌 BƯỚC 6: Module Kho hàng,POS & Hóa đơn (Inventory, POS & Billing)
*Mục tiêu: Quản lý nguồn tiền và trừ tồn kho tự động.*
- [x] Tính năng Lập hóa đơn (`invoices`) & chi tiết hóa đơn (`invoice_items`).
- [x] Tích hợp định mức tiêu hao vật tư (`service_product_usage`) tự động trừ kho (`products`) khi ca dịch vụ hoàn thành.
- [x] Tính hoa hồng (`commissions`) tự động cho Kỹ thuật viên sau khi hóa đơn được thanh toán.

### 📌 BƯỚC 7: Module CMS & Social Omnichannel Inbox
*Mục tiêu: Hoàn thiện Landing Page và chăm sóc khách hàng tập trung.*
- [x] API CMS: bài viết (`blog_posts`), khuyến mãi (`promotions`), banner (`banners`) để Next.js lấy dữ liệu hiển thị.
- [x] Tích hợp Webhook kết nối MXH (Facebook, Instagram, TikTok) để thu thập tin nhắn về bảng `conversations` và `messages`.
- [x] Kênh chat thời gian thực (Realtime Chat) qua WebSocket.
