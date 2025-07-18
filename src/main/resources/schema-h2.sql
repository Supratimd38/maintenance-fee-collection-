DROP TABLE IF EXISTS maintenance_payments;
DROP TABLE IF EXISTS flat_owners;

-- ==========================================================
--  FLAT OWNERS
-- ==========================================================
CREATE TABLE flat_owners (
    id BIGSERIAL PRIMARY KEY,        -- <── changed
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(10) NOT NULL,
    flat_number VARCHAR(50) NOT NULL UNIQUE,
    wing  VARCHAR(100) NOT NULL,
    floor INT NOT NULL,
    flat_size DECIMAL(10,2) NOT NULL,
    monthly_maintenance_amount DECIMAL(10,2) NOT NULL,
    registration_date DATE,
    is_active BOOLEAN DEFAULT TRUE
);

-- ==========================================================
--  MAINTENANCE PAYMENTS
-- ==========================================================
CREATE TABLE maintenance_payments (
    id BIGSERIAL PRIMARY KEY,        -- <── changed
    flat_owner_id BIGINT NOT NULL,
    payment_month INT  NOT NULL,
    payment_year  INT  NOT NULL,
    amount        DECIMAL(10,2) NOT NULL,
    due_date      DATE,
    payment_date  TIMESTAMP,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    razorpay_order_id   VARCHAR(100),
    razorpay_payment_id VARCHAR(100),
    razorpay_signature  VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_owner
        FOREIGN KEY (flat_owner_id)
        REFERENCES flat_owners(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_payment_status ON maintenance_payments(payment_status);
