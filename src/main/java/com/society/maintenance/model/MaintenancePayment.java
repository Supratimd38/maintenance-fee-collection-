package com.society.maintenance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_payments")
public class MaintenancePayment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flat_owner_id", nullable = false)
    private FlatOwner flatOwner;
    
    @NotNull(message = "Payment month is required")
    @Column(name = "payment_month", nullable = false)
    private Integer paymentMonth;
    
    @NotNull(message = "Payment year is required")
    @Column(name = "payment_year", nullable = false)
    private Integer paymentYear;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private Double amount;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;
    
    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;
    
    @Column(name = "razorpay_signature")
    private String razorpaySignature;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum PaymentStatus {
        PENDING, PAID, OVERDUE, FAILED
    }
    
    // Constructors
    public MaintenancePayment() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public MaintenancePayment(FlatOwner flatOwner, Integer paymentMonth, Integer paymentYear, 
                             Double amount, LocalDate dueDate) {
        this();
        this.flatOwner = flatOwner;
        this.paymentMonth = paymentMonth;
        this.paymentYear = paymentYear;
        this.amount = amount;
        this.dueDate = dueDate;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public FlatOwner getFlatOwner() { return flatOwner; }
    public void setFlatOwner(FlatOwner flatOwner) { this.flatOwner = flatOwner; }
    
    public Integer getPaymentMonth() { return paymentMonth; }
    public void setPaymentMonth(Integer paymentMonth) { this.paymentMonth = paymentMonth; }
    
    public Integer getPaymentYear() { return paymentYear; }
    public void setPaymentYear(Integer paymentYear) { this.paymentYear = paymentYear; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { 
        this.paymentStatus = paymentStatus; 
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }
    
    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}