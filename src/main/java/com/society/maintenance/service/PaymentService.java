package com.society.maintenance.service;

import com.society.maintenance.model.MaintenancePayment;
import com.society.maintenance.model.MaintenancePayment.PaymentStatus;
import com.society.maintenance.model.FlatOwner;
import com.society.maintenance.repository.MaintenancePaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {
    
    @Autowired
    private MaintenancePaymentRepository paymentRepository;
    
    @Autowired
    private FlatOwnerService flatOwnerService;
    
    public List<MaintenancePayment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public Optional<MaintenancePayment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
    
    public MaintenancePayment savePayment(MaintenancePayment payment) {
        return paymentRepository.save(payment);
    }
    
    public List<MaintenancePayment> getPendingPayments() {
        return paymentRepository.findByPaymentStatus(PaymentStatus.PENDING);
    }
    
    public List<MaintenancePayment> getOverduePayments() {
        return paymentRepository.findOverduePayments(LocalDate.now());
    }
    
    public MaintenancePayment updatePaymentStatus(Long paymentId, PaymentStatus status, 
                                                 String razorpayPaymentId, String razorpaySignature) {
        Optional<MaintenancePayment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isPresent()) {
            MaintenancePayment payment = paymentOpt.get();
            payment.setPaymentStatus(status);
            payment.setRazorpayPaymentId(razorpayPaymentId);
            payment.setRazorpaySignature(razorpaySignature);
            if (status == PaymentStatus.PAID) {
                payment.setPaymentDate(LocalDateTime.now());
            }
            return paymentRepository.save(payment);
        }
        return null;
    }
    
    public void generateMonthlyPayments(Integer month, Integer year) {
        List<FlatOwner> activeFlatOwners = flatOwnerService.getAllActiveFlatOwners();
        LocalDate dueDate = LocalDate.of(year, month, 10); // Due on 10th of each month
        
        for (FlatOwner flatOwner : activeFlatOwners) {
            // Check if payment already exists for this month/year
            Optional<MaintenancePayment> existingPayment = 
                paymentRepository.findByFlatOwnerAndMonthYear(flatOwner.getId(), month, year);
            
            if (existingPayment.isEmpty()) {
                MaintenancePayment payment = new MaintenancePayment(
                    flatOwner, month, year, flatOwner.getMonthlyMaintenanceAmount(), dueDate
                );
                paymentRepository.save(payment);
            }
        }
    }
    
    public Double getTotalCollectionForMonth(Integer month, Integer year) {
        Double total = paymentRepository.getTotalCollectionForMonth(month, year);
        return total != null ? total : 0.0;
    }
    
    public Long getPendingPaymentCount() {
        return paymentRepository.countByPaymentStatus(PaymentStatus.PENDING);
    }
    
    public Long getPaidPaymentCount() {
        return paymentRepository.countByPaymentStatus(PaymentStatus.PAID);
    }
}