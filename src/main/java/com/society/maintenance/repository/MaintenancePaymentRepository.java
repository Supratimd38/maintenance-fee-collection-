package com.society.maintenance.repository;

import com.society.maintenance.model.MaintenancePayment;
import com.society.maintenance.model.MaintenancePayment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenancePaymentRepository extends JpaRepository<MaintenancePayment, Long> {
    
    List<MaintenancePayment> findByFlatOwnerIdAndPaymentStatus(Long flatOwnerId, PaymentStatus status);
    
    List<MaintenancePayment> findByPaymentMonthAndPaymentYear(Integer month, Integer year);
    
    List<MaintenancePayment> findByPaymentStatus(PaymentStatus status);
    
    Optional<MaintenancePayment> findByRazorpayOrderId(String orderId);
    
    @Query("SELECT p FROM MaintenancePayment p WHERE p.flatOwner.id = :flatOwnerId " +
           "AND p.paymentMonth = :month AND p.paymentYear = :year")
    Optional<MaintenancePayment> findByFlatOwnerAndMonthYear(@Param("flatOwnerId") Long flatOwnerId,
                                                            @Param("month") Integer month,
                                                            @Param("year") Integer year);
    
    @Query("SELECT p FROM MaintenancePayment p WHERE p.dueDate < :currentDate " +
           "AND p.paymentStatus = 'PENDING'")
    List<MaintenancePayment> findOverduePayments(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT SUM(p.amount) FROM MaintenancePayment p WHERE p.paymentStatus = 'PAID' " +
           "AND p.paymentMonth = :month AND p.paymentYear = :year")
    Double getTotalCollectionForMonth(@Param("month") Integer month, @Param("year") Integer year);
    
    @Query("SELECT COUNT(p) FROM MaintenancePayment p WHERE p.paymentStatus = :status")
    Long countByPaymentStatus(@Param("status") PaymentStatus status);
}