# BÁO CÁO TIẾN ĐỘ - BƯỚC 2: BẢO MẬT & XÁC THỰC VỚI JWT (SPRING SECURITY)

Chúng tôi đã hoàn thành **Bước 2** theo lộ trình phát triển hệ thống quản lý Spa. Dưới đây là báo cáo chi tiết về tiến độ và các logic bảo mật/xác thực đã được triển khai trong từng file ở máy local của bạn.

*(Lưu ý: Theo yêu cầu của bạn, các thay đổi này mới chỉ lưu ở máy local và CHƯA được push lên Git để bạn kiểm tra trước khi tạo 1 commit gộp).*

---

## 🛠️ CÁC LOGIC VÀ PHẦN MỀM ĐẠ TRIỂN KHAI

### 1. Cập nhật Thư viện & Phụ thuộc
*   **[pom.xml](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/pom.xml)**:
    *   Thêm thư viện `spring-boot-starter-security` để kích hoạt Spring Security.
    *   Thêm các thư viện JWT (`jjwt-api`, `jjwt-impl`, `jjwt-jackson` bản `0.11.5`) để hỗ trợ sinh và giải mã mã xác thực JWT token.

### 2. Cấu hình Tài khoản Người dùng (User Details)
*   **[EmployeePrincipal.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/security/EmployeePrincipal.java)**:
    *   *Nhiệm vụ*: Lớp chuyển đổi thực thể `Employee` thành đối tượng `UserDetails` tương thích với Spring Security.
    *   *Logic*:
        *   Trả về email làm tên đăng nhập (`username`) và password hash làm mật khẩu (`password`).
        *   Tự động ánh xạ từ Role thực thể sang quyền hạn trong Security bằng cách thêm tiền tố `"ROLE_" + role.getCode()`.
        *   Kiểm tra trạng thái tài khoản: bị khóa nếu `status` là `SUSPENDED`, không hoạt động nếu trạng thái không phải `ACTIVE`.
*   **[CustomUserDetailsService.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/security/CustomUserDetailsService.java)**:
    *   *Nhiệm vụ*: Load thông tin tài khoản nhân viên từ Database.
    *   *Logic*: Implements `UserDetailsService`, định nghĩa hàm `loadUserByUsername(String email)` gọi đến `EmployeeRepository` để tìm kiếm nhân viên theo Email.
*   **[EmployeeRepository.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/repository/EmployeeRepository.java)**:
    *   *Nhiệm vụ*: Interface truy vấn database cho bảng `employees`.
    *   *Logic*: Bổ sung hàm truy vấn `findByEmail(String email)` để phục vụ xác thực tài khoản.

### 3. Xử lý Token JWT (Authentication & Security Filter)
*   **[JwtTokenProvider.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/security/JwtTokenProvider.java)**:
    *   *Nhiệm vụ*: Trình quản lý sinh/xác thực/giải mã JWT token.
    *   *Logic*: 
        *   Sử dụng thuật toán ký `HS256` với khóa bí mật bảo mật tối thiểu 256-bit được cấu hình (hoặc fallback mặc định).
        *   Hàm `generateToken` mã hóa thông tin Email của nhân viên vào Payload và thiết lập thời gian hết hạn (mặc định 24h).
        *   Hàm `validateToken` kiểm tra tính hợp lệ của token (chữ ký, thời hạn).
*   **[JwtAuthenticationFilter.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/security/JwtAuthenticationFilter.java)**:
    *   *Nhiệm vụ*: Bộ lọc HTTP chặn các request để xác thực token JWT.
    *   *Logic*: Kế thừa `OncePerRequestFilter`. Đọc token trong header `Authorization` (dạng `Bearer <Token>`), giải mã email, load `UserDetails` và đăng ký thông tin xác thực vào `SecurityContextHolder`.
*   **[SecurityConfig.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/security/SecurityConfig.java)**:
    *   *Nhiệm vụ*: Lớp cấu hình phân quyền và bộ lọc bảo mật chính của Spring Security 6.
    *   *Logic*:
        *   Sử dụng `@EnableMethodSecurity` để cho phép phân quyền chi tiết trực tiếp trên các hàm Controller (ví dụ `@PreAuthorize("hasRole('ADMIN')")`).
        *   Vô hiệu hóa CSRF (do cơ chế stateless của JWT) và cấu hình CORS.
        *   Phân quyền: Mở cổng công khai cho các API login (`/api/auth/**`) và xem thông tin dịch vụ (`GET /api/services/**`, `GET /api/categories/**`). Bắt buộc xác thực với mọi request còn lại.
        *   Đăng ký `JwtAuthenticationFilter` chạy trước bộ lọc đăng nhập mặc định của Spring.
        *   Khai báo Bean `BCryptPasswordEncoder` để băm mật khẩu.

### 4. API Đăng nhập (Auth Controller & DTOs)
*   **[LoginRequest.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/dto/LoginRequest.java)**: DTO nhận thông tin email & password gửi lên từ Client.
*   **[JwtResponse.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/dto/JwtResponse.java)**: DTO trả về thông tin Token kèm theo thông tin cơ bản của nhân viên đăng nhập (Họ tên, Email, Vai trò).
*   **[AuthController.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/controller/AuthController.java)**:
    *   *Nhiệm vụ*: Cung cấp API đăng nhập cho nhân viên tại `/api/auth/login`.
    *   *Logic*: Gọi `AuthenticationManager` để xác thực tài khoản. Nếu đúng mật khẩu và tài khoản hoạt động, tiến hành sinh JWT Token qua `JwtTokenProvider` và trả về kết quả thành công đóng gói trong `ApiResponse`.

---

## 📈 TIẾN TRÌNH THỰC HIỆN KẾ HOẠCH
- [x] Tích hợp Spring Security & JWT dependencies trong `pom.xml`
- [x] Tạo `EmployeeRepository` & `EmployeePrincipal`
- [x] Tạo dịch vụ `CustomUserDetailsService`
- [x] Triển khai bộ sinh/giải mã token `JwtTokenProvider`
- [x] Viết bộ lọc xác thực `JwtAuthenticationFilter`
- [x] Cấu hình bảo mật hệ thống `SecurityConfig`
- [x] Tạo REST Controller `/api/auth/login`
- [x] Chạy biên dịch thử nghiệm nội bộ (`BUILD SUCCESS` trên Docker)
