package com.society.maintenance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "flat_owners")
public class FlatOwner {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
    
    @NotBlank(message = "Flat number is required")
    @Column(unique = true, nullable = false)
    private String flatNumber;
    
    @NotBlank(message = "Wing is required")
    private String wing;
    
    @NotNull(message = "Floor is required")
    private Integer floor;
    
    @NotNull(message = "Flat size is required")
    private Double flatSize;
    
    @NotNull(message = "Monthly maintenance amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private Double monthlyMaintenanceAmount;
    
    @Column(name = "registration_date")
    private LocalDate registrationDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "flatOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaintenancePayment> payments;
    
    // Constructors
    public FlatOwner() {
        this.registrationDate = LocalDate.now();
    }
    
    public FlatOwner(String name, String email, String phoneNumber, String flatNumber, 
                    String wing, Integer floor, Double flatSize, Double monthlyMaintenanceAmount) {
        this();
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.flatNumber = flatNumber;
        this.wing = wing;
        this.floor = floor;
        this.flatSize = flatSize;
        this.monthlyMaintenanceAmount = monthlyMaintenanceAmount;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }
    
    public String getWing() { return wing; }
    public void setWing(String wing) { this.wing = wing; }
    
    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }
    
    public Double getFlatSize() { return flatSize; }
    public void setFlatSize(Double flatSize) { this.flatSize = flatSize; }
    
    public Double getMonthlyMaintenanceAmount() { return monthlyMaintenanceAmount; }
    public void setMonthlyMaintenanceAmount(Double monthlyMaintenanceAmount) { 
        this.monthlyMaintenanceAmount = monthlyMaintenanceAmount; 
    }
    
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public List<MaintenancePayment> getPayments() { return payments; }
    public void setPayments(List<MaintenancePayment> payments) { this.payments = payments; }
}