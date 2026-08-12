-- =====================================================================
-- SPA MANAGEMENT SYSTEM - DATABASE SCHEMA
-- Stack: Spring Boot (Java) + Next.js + MySQL 8.x
-- Charset: utf8mb4 | Engine: InnoDB
-- =====================================================================

SET FOREIGN_KEY_CHECKS = 0;
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS spa_management
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE spa_management;

-- =====================================================================
-- MODULE 3: NHÂN VIÊN & VAI TRÒ (STAFF)
-- Tạo trước vì hầu hết các bảng khác đều tham chiếu employees
-- =====================================================================

CREATE TABLE roles (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(50)     NOT NULL UNIQUE COMMENT 'ADMIN, RECEPTIONIST, TECHNICIAN',
    name            VARCHAR(100)    NOT NULL,
    description     VARCHAR(255)    NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE employees (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    role_id         BIGINT UNSIGNED NOT NULL,
    employee_code   VARCHAR(30)     NOT NULL UNIQUE,
    full_name       VARCHAR(150)    NOT NULL,
    email           VARCHAR(150)    NOT NULL UNIQUE,
    phone           VARCHAR(20)     NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    avatar_url      VARCHAR(500)    NULL,
    gender          ENUM('MALE','FEMALE','OTHER') NULL,
    dob             DATE            NULL,
    hire_date       DATE            NULL,
    base_salary     DECIMAL(14,2)   NULL,
    status          ENUM('ACTIVE','INACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_employees_role FOREIGN KEY (role_id) REFERENCES roles(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_employees_role (role_id)
) ENGINE=InnoDB;

CREATE TABLE shifts (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL COMMENT 'Ca sáng, Ca chiều, Ca tối',
    start_time      TIME            NOT NULL,
    end_time        TIME            NOT NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE employee_shifts (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    employee_id     BIGINT UNSIGNED NOT NULL,
    shift_id        BIGINT UNSIGNED NOT NULL,
    work_date       DATE            NOT NULL,
    status          ENUM('ASSIGNED','CHECKED_IN','CHECKED_OUT','ABSENT') NOT NULL DEFAULT 'ASSIGNED',
    check_in_time   DATETIME        NULL,
    check_out_time  DATETIME        NULL,
    note            VARCHAR(255)    NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_empshift_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_empshift_shift FOREIGN KEY (shift_id) REFERENCES shifts(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    UNIQUE KEY uq_employee_shift_date (employee_id, shift_id, work_date),
    INDEX idx_empshift_date (work_date)
) ENGINE=InnoDB;

-- =====================================================================
-- MODULE 1: KHÁCH HÀNG & THẺ THÀNH VIÊN (CRM)
-- =====================================================================

CREATE TABLE membership_tiers (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(30)     NOT NULL UNIQUE COMMENT 'GOLD, SILVER, BRONZE',
    name            VARCHAR(100)    NOT NULL,
    min_points      INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT 'Điểm tối thiểu để đạt hạng',
    discount_percent DECIMAL(5,2)   NOT NULL DEFAULT 0.00,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE customers (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    membership_tier_id  BIGINT UNSIGNED NULL,
    customer_code       VARCHAR(30)     NOT NULL UNIQUE,
    full_name           VARCHAR(150)    NOT NULL,
    phone               VARCHAR(20)     NOT NULL UNIQUE,
    email                VARCHAR(150)    NULL,
    dob                 DATE            NULL,
    gender              ENUM('MALE','FEMALE','OTHER') NULL,
    address             VARCHAR(255)    NULL,
    avatar_url          VARCHAR(500)    NULL,
    loyalty_points      INT UNSIGNED    NOT NULL DEFAULT 0,
    skin_type           VARCHAR(100)    NULL COMMENT 'Da dầu, Da khô, Da hỗn hợp...',
    medical_notes       TEXT            NULL COMMENT 'Hồ sơ bệnh lý / dị ứng',
    source               VARCHAR(50)     NULL COMMENT 'Walk-in, Facebook, TikTok, Referral...',
    status               ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_customers_tier FOREIGN KEY (membership_tier_id) REFERENCES membership_tiers(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_customers_tier (membership_tier_id),
    INDEX idx_customers_phone (phone)
) ENGINE=InnoDB;

-- =====================================================================
-- MODULE 4: DỊCH VỤ & PHÒNG/GIƯỜNG
-- =====================================================================

CREATE TABLE service_categories (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    description     VARCHAR(255)    NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE services (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_id     BIGINT UNSIGNED NOT NULL,
    name            VARCHAR(150)    NOT NULL,
    description     TEXT            NULL,
    price           DECIMAL(14,2)   NOT NULL DEFAULT 0.00,
    duration_minutes INT UNSIGNED   NOT NULL DEFAULT 60,
    image_url       VARCHAR(500)    NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_services_category FOREIGN KEY (category_id) REFERENCES service_categories(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_services_category (category_id)
) ENGINE=InnoDB;

CREATE TABLE rooms (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    floor           VARCHAR(30)     NULL,
    status          ENUM('AVAILABLE','MAINTENANCE','INACTIVE') NOT NULL DEFAULT 'AVAILABLE',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE beds (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    room_id         BIGINT UNSIGNED NOT NULL,
    name            VARCHAR(100)    NOT NULL COMMENT 'Giường 1, Giường 2...',
    status          ENUM('AVAILABLE','MAINTENANCE','INACTIVE') NOT NULL DEFAULT 'AVAILABLE',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_beds_room FOREIGN KEY (room_id) REFERENCES rooms(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_beds_room (room_id)
) ENGINE=InnoDB;

-- =====================================================================
-- MODULE 2: ĐẶT LỊCH DỊCH VỤ (BOOKING)
-- =====================================================================

CREATE TABLE appointments (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT UNSIGNED NOT NULL,
    bed_id          BIGINT UNSIGNED NULL,
    primary_employee_id BIGINT UNSIGNED NULL COMMENT 'KTV chính phụ trách lịch hẹn',
    created_by      BIGINT UNSIGNED NULL COMMENT 'Nhân viên/lễ tân tạo lịch',
    appointment_date DATE           NOT NULL,
    start_time      DATETIME        NOT NULL,
    end_time        DATETIME        NOT NULL,
    status          ENUM('PENDING','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    source          ENUM('ONLINE','OFFLINE') NOT NULL DEFAULT 'OFFLINE',
    note            VARCHAR(500)    NULL,
    cancelled_reason VARCHAR(255)   NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointments_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_appointments_bed FOREIGN KEY (bed_id) REFERENCES beds(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_appointments_employee FOREIGN KEY (primary_employee_id) REFERENCES employees(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_appointments_created_by FOREIGN KEY (created_by) REFERENCES employees(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_appointments_customer (customer_id),
    INDEX idx_appointments_bed_time (bed_id, start_time, end_time),
    INDEX idx_appointments_date_status (appointment_date, status)
) ENGINE=InnoDB;

-- Bảng trung gian N-N giữa appointments và services
CREATE TABLE appointment_services (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    appointment_id  BIGINT UNSIGNED NOT NULL,
    service_id      BIGINT UNSIGNED NOT NULL,
    employee_id     BIGINT UNSIGNED NULL COMMENT 'KTV thực hiện dịch vụ này',
    quantity        INT UNSIGNED    NOT NULL DEFAULT 1,
    unit_price      DECIMAL(14,2)   NOT NULL COMMENT 'Giá tại thời điểm đặt (snapshot)',
    status          ENUM('PENDING','DONE','CANCELLED') NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_appsvc_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_appsvc_service FOREIGN KEY (service_id) REFERENCES services(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_appsvc_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_appsvc_appointment (appointment_id),
    INDEX idx_appsvc_employee (employee_id)
) ENGINE=InnoDB;

-- Hồ sơ trị liệu
CREATE TABLE treatment_history (
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    customer_id             BIGINT UNSIGNED NOT NULL,
    appointment_service_id  BIGINT UNSIGNED NULL,
    employee_id             BIGINT UNSIGNED NULL COMMENT 'KTV ghi nhận hồ sơ',
    treatment_date          DATE            NOT NULL,
    notes                   TEXT            NULL,
    before_image_url        VARCHAR(500)    NULL,
    after_image_url         VARCHAR(500)    NULL,
    created_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_treathist_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_treathist_appsvc FOREIGN KEY (appointment_service_id) REFERENCES appointment_services(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_treathist_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_treathist_customer (customer_id)
) ENGINE=InnoDB;

-- =====================================================================
-- MODULE 3 (tiếp): HOA HỒNG (COMMISSION)
-- =====================================================================

CREATE TABLE service_commission_rates (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    service_id      BIGINT UNSIGNED NOT NULL,
    employee_id     BIGINT UNSIGNED NULL COMMENT 'NULL = áp dụng mặc định cho mọi KTV',
    commission_percent DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_commrate_service FOREIGN KEY (service_id) REFERENCES services(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_commrate_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE KEY uq_service_employee_rate (service_id, employee_id)
) ENGINE=InnoDB;

CREATE TABLE commissions (
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    appointment_service_id  BIGINT UNSIGNED NOT NULL,
    employee_id             BIGINT UNSIGNED NOT NULL,
    invoice_id              BIGINT UNSIGNED NULL,
    rate_applied            DECIMAL(5,2)    NOT NULL,
    amount                  DECIMAL(14,2)   NOT NULL,
    status                  ENUM('PENDING','APPROVED','PAID') NOT NULL DEFAULT 'PENDING',
    created_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_comm_appsvc FOREIGN KEY (appointment_service_id) REFERENCES appointment_services(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_comm_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_comm_employee (employee_id),
    INDEX idx_comm_invoice (invoice_id)
) ENGINE=InnoDB;

-- =====================================================================
-- MODULE 5: VẬT TƯ & KHO (INVENTORY)
-- =====================================================================

CREATE TABLE suppliers (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(150)    NOT NULL,
    phone           VARCHAR(20)     NULL,
    address         VARCHAR(255)    NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE products (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    supplier_id     BIGINT UNSIGNED NULL,
    sku             VARCHAR(50)     NOT NULL UNIQUE,
    name            VARCHAR(150)    NOT NULL,
    unit            VARCHAR(30)     NOT NULL COMMENT 'chai, hộp, ml, gram...',
    category        ENUM('SUPPLY','RETAIL','BOTH') NOT NULL DEFAULT 'SUPPLY',
    cost_price      DECIMAL(14,2)   NOT NULL DEFAULT 0.00,
    retail_price    DECIMAL(14,2)   NULL COMMENT 'Giá bán lẻ, NULL nếu chỉ dùng nội bộ',
    current_stock   DECIMAL(14,2)   NOT NULL DEFAULT 0,
    min_stock_level DECIMAL(14,2)   NOT NULL DEFAULT 0 COMMENT 'Ngưỡng cảnh báo tồn kho tối thiểu',
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_products_stock (current_stock, min_stock_level)
) ENGINE=InnoDB;

CREATE TABLE service_product_usage (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    service_id      BIGINT UNSIGNED NOT NULL,
    product_id      BIGINT UNSIGNED NOT NULL,
    quantity_used   DECIMAL(14,2)   NOT NULL DEFAULT 0 COMMENT 'Số lượng tiêu hao mỗi lần thực hiện dịch vụ',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_spu_service FOREIGN KEY (service_id) REFERENCES services(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_spu_product FOREIGN KEY (product_id) REFERENCES products(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE KEY uq_service_product (service_id, product_id)
) ENGINE=InnoDB;

CREATE TABLE stock_transactions (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT UNSIGNED NOT NULL,
    employee_id     BIGINT UNSIGNED NULL COMMENT 'Người thực hiện phiếu',
    type            ENUM('IMPORT','EXPORT','ADJUSTMENT') NOT NULL,
    reference_type  ENUM('PURCHASE','SERVICE_USAGE','RETAIL_SALE','ADJUSTMENT') NOT NULL,
    reference_id    BIGINT UNSIGNED NULL COMMENT 'ID hóa đơn/appointment_service liên quan (nếu có)',
    quantity        DECIMAL(14,2)   NOT NULL COMMENT 'Luôn dương, dấu +/- xác định bởi type',
    note            VARCHAR(255)    NULL,
    transaction_date DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_stocktrans_product FOREIGN KEY (product_id) REFERENCES products(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_stocktrans_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_stocktrans_product (product_id),
    INDEX idx_stocktrans_date (transaction_date)
) ENGINE=InnoDB;

-- =====================================================================
-- MODULE 6: HÓA ĐƠN & THANH TOÁN (POS & BILLING)
-- =====================================================================

CREATE TABLE payment_methods (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(30)     NOT NULL UNIQUE COMMENT 'CASH, BANK_TRANSFER, CARD, EWALLET',
    name            VARCHAR(100)    NOT NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE invoices (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    invoice_code        VARCHAR(30)     NOT NULL UNIQUE,
    customer_id         BIGINT UNSIGNED NOT NULL,
    appointment_id      BIGINT UNSIGNED NULL,
    cashier_id          BIGINT UNSIGNED NULL COMMENT 'Nhân viên thu ngân lập hóa đơn',
    subtotal_amount     DECIMAL(14,2)   NOT NULL DEFAULT 0.00,
    discount_amount     DECIMAL(14,2)   NOT NULL DEFAULT 0.00,
    tax_amount          DECIMAL(14,2)   NOT NULL DEFAULT 0.00,
    total_amount        DECIMAL(14,2)   NOT NULL DEFAULT 0.00,
    points_earned       INT UNSIGNED    NOT NULL DEFAULT 0,
    points_used         INT UNSIGNED    NOT NULL DEFAULT 0,
    status              ENUM('DRAFT','UNPAID','PARTIALLY_PAID','PAID','CANCELLED') NOT NULL DEFAULT 'DRAFT',
    note                VARCHAR(255)    NULL,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoices_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_invoices_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_invoices_cashier FOREIGN KEY (cashier_id) REFERENCES employees(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_invoices_customer (customer_id),
    INDEX idx_invoices_status (status)
) ENGINE=InnoDB;

CREATE TABLE invoice_items (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    invoice_id      BIGINT UNSIGNED NOT NULL,
    item_type       ENUM('SERVICE','PRODUCT') NOT NULL,
    service_id      BIGINT UNSIGNED NULL,
    product_id      BIGINT UNSIGNED NULL,
    quantity        INT UNSIGNED    NOT NULL DEFAULT 1,
    unit_price      DECIMAL(14,2)   NOT NULL,
    discount_amount DECIMAL(14,2)   NOT NULL DEFAULT 0.00,
    total_price     DECIMAL(14,2)   NOT NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_invitems_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_invitems_service FOREIGN KEY (service_id) REFERENCES services(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_invitems_product FOREIGN KEY (product_id) REFERENCES products(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_invitems_invoice (invoice_id)
) ENGINE=InnoDB;

CREATE TABLE payments (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    invoice_id          BIGINT UNSIGNED NOT NULL,
    payment_method_id   BIGINT UNSIGNED NOT NULL,
    amount              DECIMAL(14,2)   NOT NULL,
    transaction_ref      VARCHAR(150)    NULL COMMENT 'Mã giao dịch ngân hàng/ví điện tử',
    paid_at             DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_payments_method FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_payments_invoice (invoice_id)
) ENGINE=InnoDB;

-- =====================================================================
-- MODULE 7: QUẢN LÝ NỘI DUNG (CMS)
-- =====================================================================

CREATE TABLE blog_posts (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    author_id       BIGINT UNSIGNED NULL,
    title           VARCHAR(255)    NOT NULL,
    slug            VARCHAR(255)    NOT NULL UNIQUE,
    excerpt         VARCHAR(500)    NULL,
    content         LONGTEXT        NOT NULL,
    thumbnail_url   VARCHAR(500)    NULL,
    status          ENUM('DRAFT','PUBLISHED','ARCHIVED') NOT NULL DEFAULT 'DRAFT',
    published_at    DATETIME        NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_blogposts_author FOREIGN KEY (author_id) REFERENCES employees(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_blogposts_status (status, published_at)
) ENGINE=InnoDB;

CREATE TABLE promotions (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(255)    NOT NULL,
    slug            VARCHAR(255)    NOT NULL UNIQUE,
    description     TEXT            NULL,
    discount_type   ENUM('PERCENT','FIXED') NOT NULL DEFAULT 'PERCENT',
    discount_value  DECIMAL(14,2)   NOT NULL DEFAULT 0.00,
    banner_image_url VARCHAR(500)   NULL,
    start_date      DATETIME        NOT NULL,
    end_date        DATETIME        NOT NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_promotions_active_dates (is_active, start_date, end_date)
) ENGINE=InnoDB;

CREATE TABLE banners (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(255)    NOT NULL,
    image_url       VARCHAR(500)    NOT NULL,
    link_url        VARCHAR(500)    NULL,
    position        VARCHAR(50)     NULL COMMENT 'HOME_HERO, HOME_MID, SIDEBAR...',
    display_order   INT             NOT NULL DEFAULT 0,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    start_date      DATETIME        NULL,
    end_date        DATETIME        NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_banners_position (position, display_order)
) ENGINE=InnoDB;

-- =====================================================================
-- MODULE 8: HỘP THƯ TẬP TRUNG (OMNICHANNEL SOCIAL INBOX)
-- =====================================================================

CREATE TABLE social_accounts (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    platform        ENUM('FACEBOOK','INSTAGRAM','TIKTOK','ZALO') NOT NULL,
    account_name    VARCHAR(150)    NOT NULL,
    external_page_id VARCHAR(150)   NOT NULL COMMENT 'Page ID / Account ID trên nền tảng',
    access_token    VARCHAR(1000)   NULL COMMENT 'Nên mã hóa trước khi lưu',
    connected_by    BIGINT UNSIGNED NULL,
    status          ENUM('CONNECTED','DISCONNECTED','EXPIRED') NOT NULL DEFAULT 'CONNECTED',
    connected_at    DATETIME        NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_socialacc_employee FOREIGN KEY (connected_by) REFERENCES employees(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    UNIQUE KEY uq_platform_page (platform, external_page_id)
) ENGINE=InnoDB;

CREATE TABLE conversations (
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    social_account_id       BIGINT UNSIGNED NOT NULL,
    customer_id             BIGINT UNSIGNED NULL COMMENT 'Liên kết tới CRM nếu đã nhận diện được khách',
    external_conversation_id VARCHAR(150)   NOT NULL,
    customer_social_name    VARCHAR(150)    NULL,
    customer_avatar_url     VARCHAR(500)    NULL,
    assigned_employee_id    BIGINT UNSIGNED NULL,
    status                  ENUM('OPEN','PENDING','CLOSED') NOT NULL DEFAULT 'OPEN',
    last_message_at         DATETIME        NULL,
    created_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_conv_socialaccount FOREIGN KEY (social_account_id) REFERENCES social_accounts(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_conv_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_conv_employee FOREIGN KEY (assigned_employee_id) REFERENCES employees(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    UNIQUE KEY uq_account_conversation (social_account_id, external_conversation_id),
    INDEX idx_conv_customer (customer_id),
    INDEX idx_conv_last_message (last_message_at)
) ENGINE=InnoDB;

CREATE TABLE messages (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    conversation_id     BIGINT UNSIGNED NOT NULL,
    sender_type         ENUM('CUSTOMER','EMPLOYEE','SYSTEM') NOT NULL,
    employee_id         BIGINT UNSIGNED NULL COMMENT 'Chỉ có giá trị khi sender_type = EMPLOYEE',
    content             TEXT            NULL,
    attachment_url      VARCHAR(500)    NULL,
    external_message_id VARCHAR(150)    NULL,
    is_read             BOOLEAN         NOT NULL DEFAULT FALSE,
    sent_at             DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_messages_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_messages_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_messages_conversation (conversation_id, sent_at)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- SEED DATA TỐI THIỂU
-- =====================================================================

INSERT INTO roles (code, name) VALUES
    ('ADMIN', 'Quản trị viên'),
    ('RECEPTIONIST', 'Lễ tân'),
    ('TECHNICIAN', 'Kỹ thuật viên');

INSERT INTO membership_tiers (code, name, min_points, discount_percent) VALUES
    ('BRONZE', 'Đồng', 0, 0),
    ('SILVER', 'Bạc', 1000, 5),
    ('GOLD', 'Vàng', 5000, 10);

INSERT INTO payment_methods (code, name) VALUES
    ('CASH', 'Tiền mặt'),
    ('BANK_TRANSFER', 'Chuyển khoản'),
    ('CARD', 'Thẻ ngân hàng'),
    ('EWALLET', 'Ví điện tử');

INSERT INTO employees (role_id, employee_code, full_name, email, phone, password_hash, status) VALUES
    (1, 'EMP001', 'Spa Administrator', 'admin@spa.com', '0987654321', '$2a$10$Nz6K97.q78Qn430Cq9Gxe.kKsnR7B2jLpWecR0FmU6d2Yp0/J.QNu', 'ACTIVE');
