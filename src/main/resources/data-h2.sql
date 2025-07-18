-- ───── Flat-owners ────────────────────────────────────────
INSERT INTO flat_owners
      (name,email,phone_number,flat_number,wing,floor,flat_size,
       monthly_maintenance_amount,registration_date,is_active)
VALUES('Amit Sharma','amit@example.com','9876543210','A-101','A',1,1050,2500,'2024-01-15',TRUE),
      ('Neha Verma','neha@example.com','9123456780','B-203','B',2,1150,2700,'2024-02-10',TRUE),
      ('Rahul Gupta','rahul@example.com','9988776655','C-305','C',3,950,2300,'2024-03-12',TRUE);

-- ───── Maintenance payments ───────────────────────────────
--  Flat-owner id mapping: Amit=1, Neha=2, Rahul=3
INSERT INTO maintenance_payments
 (flat_owner_id,payment_month,payment_year,amount,due_date,payment_status)
VALUES
 (1,7,2025,2500,'2025-07-10','PENDING'),
 (1,6,2025,2500,'2025-06-10','PAID'),
 (2,7,2025,2700,'2025-07-10','PENDING'),
 (2,6,2025,2700,'2025-06-10','PAID'),
 (3,7,2025,2300,'2025-07-10','PENDING'),
 (3,6,2025,2300,'2025-06-10','OVERDUE');
