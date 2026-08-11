# BÁO CÁO TIẾN ĐỘ - BƯỚC 5: MODULE ĐẶT LỊCH (BOOKING)

Chúng tôi đã hoàn thành **Bước 5** theo lộ trình phát triển hệ thống quản lý Spa. Dưới đây là báo cáo chi tiết về tiến độ và các logic nghiệp vụ đặt lịch/trùng lịch đã được triển khai trong từng file ở máy local của bạn.

*(Lưu ý: Theo yêu cầu của bạn, các thay đổi này mới chỉ lưu ở máy local và CHƯA được push lên Git để bạn kiểm tra trước khi tạo 1 commit gộp).*

---

## 🛠️ CÁC LOGIC VÀ PHẦN MỀM ĐÃ TRIỂN KHAI

### 1. Tương tác Cơ sở dữ liệu (Repository Layer)
*   **[AppointmentRepository.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/repository/AppointmentRepository.java)**:
    *   *Nhiệm vụ*: Tương tác bảng `appointments`.
    *   *Logic*:
        *   Tìm kiếm danh sách lịch hẹn của khách hàng (`findByCustomerId`).
        *   Tìm kiếm danh sách lịch hẹn trong ngày (`findByAppointmentDate`).
        *   Tìm các lịch hẹn đang hoạt động (không bị hủy) của một kỹ thuật viên chính (`findByPrimaryEmployeeIdAndAppointmentDateAndStatusNot`).
*   **[AppointmentServiceRepository.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/repository/AppointmentServiceRepository.java)**:
    *   *Nhiệm vụ*: Tương tác bảng `appointment_services` (Dịch vụ đi kèm lịch hẹn).
    *   *Logic*: Tìm kiếm các dịch vụ được phân công cho một kỹ thuật viên cụ thể trên một ngày chỉ định (`findByEmployeeIdAndAppointmentAppointmentDateAndAppointmentStatusNot`).

---

### 2. Các lớp truyền tải dữ liệu (DTO Layer)
*   **[AppointmentDto.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/dto/AppointmentDto.java)**: Trả về thông tin đầy đủ của cuộc hẹn (thông tin khách hàng, kỹ thuật viên chính, thời gian bắt đầu/kết thúc, trạng thái và danh sách các dịch vụ chi tiết).
*   **[AppointmentServiceDto.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/dto/AppointmentServiceDto.java)**: Chi tiết dịch vụ được đặt (tên dịch vụ, kỹ thuật viên phụ trách dịch vụ, giá tiền, trạng thái dịch vụ).
*   **[BookingRequestDto.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/dto/BookingRequestDto.java)**: Chứa thông tin client gửi lên để thực hiện đặt lịch (ID khách hàng, danh sách ID dịch vụ đặt kèm kỹ thuật viên yêu cầu nếu có, thời gian bắt đầu).

---

### 3. Nghiệp vụ logic đặt lịch & Xử lý trùng ca (Service Layer)
*   **[AppointmentService.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/service/AppointmentService.java)** & **[AppointmentServiceImpl.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/service/AppointmentServiceImpl.java)**:
    *   *Logic*:
        *   `createBooking`: Nhận yêu cầu đặt lịch, tính tổng thời lượng của các dịch vụ để tự động cộng vào `startTime` sinh ra `endTime`. Xác thực xem khách hàng, dịch vụ có hợp lệ không.
        *   `isTechnicianAvailable` (Xử lý trùng lịch): 
            1. Kiểm tra kỹ thuật viên phụ trách có ca trực hợp lệ trong ngày trực (`ASSIGNED` hoặc `CHECKED_IN` tại bảng `employee_shifts`).
            2. Kiểm tra xem kỹ thuật viên có bị trùng lịch hẹn với bất kỳ lịch hẹn nào khác đang chạy (kiểm tra khoảng thời gian bắt đầu/kết thúc có bị giao nhau - Overlapping - với lịch hẹn chính hoặc lịch hẹn phụ vụ dịch vụ cụ thể của kỹ thuật viên đó hay không).
        *   `updateAppointmentStatus` (Quy trình trạng thái): Cho phép chuyển đổi các trạng thái `PENDING` -> `CONFIRMED` -> `COMPLETED` / `CANCELLED`. Nếu hủy lịch (`CANCELLED`), tự động cập nhật lý do và hủy toàn bộ các dịch vụ con đi kèm. Nếu hoàn tất (`COMPLETED`), tự động đổi trạng thái toàn bộ dịch vụ con thành `DONE`.
        *   *Lưu ý giải quyết Name Collision*: Vì có sự trùng tên giữa interface Service (`AppointmentService`) và JPA Entity Model (`com.example.demodevops.model.AppointmentService`), chúng tôi đã sử dụng đường dẫn package đầy đủ trong code triển khai để đảm bảo Java compiler biên dịch chính xác 100%.

---

### 4. Giao diện REST API (Controller Layer)
*   **[AppointmentController.java](file:///c:/Users/Admin/Desktop/Du%20an%20web/TestCD/src/main/java/com/example/demodevops/controller/AppointmentController.java)**:
    *   Đường dẫn: `/api/appointments`.
    *   *Bảo mật*: Phân quyền truy cập bằng JWT của Spring Security.
    *   *Endpoints*:
        *   `POST /api/appointments/book`: Đặt lịch mới (tự động điền ID nhân viên tạo lịch thông qua token JWT người dùng đăng nhập hiện tại).
        *   `PUT /api/appointments/{id}/status`: Cập nhật trạng thái lịch hẹn.
        *   `GET /api/appointments/check-availability`: API kiểm tra xem kỹ thuật viên có rảnh trong khoảng thời gian xác định để hỗ trợ đặt lịch tự động từ UI.
        *   `GET /api/appointments/date/{date}`: Xem lịch hẹn trong ngày.

---

## 📈 TIẾN TRÌNH THỰC HIỆN KẾ HOẠCH
- [x] Tạo Repositories quản lý Lịch hẹn & Dịch vụ đặt kèm
- [x] Tạo cấu trúc Booking DTOs nhận dữ liệu
- [x] Thiết lập thuật toán kiểm tra trùng lịch (overlapping check) cho kỹ thuật viên
- [x] Xác thực ca trực (EmployeeShift) khi đặt lịch
- [x] Quản lý luồng cập nhật trạng thái (Hủy lịch, hoàn thành lịch)
- [x] Biên dịch kiểm thử thành công (`BUILD SUCCESS`)
