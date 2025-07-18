package com.society.maintenance.controller;

import com.society.maintenance.model.MaintenancePayment;
import com.society.maintenance.model.MaintenancePayment.PaymentStatus;
import com.society.maintenance.service.PaymentService;
import com.society.maintenance.service.RazorpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Optional;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private RazorpayService razorpayService;
    
    @Value("${razorpay.key.id}")
    private String keyId;
    
 // PaymentController snippet
    @GetMapping("/pay/{id}")
    public String paymentPage(@PathVariable Long id, Model model) {
        return paymentService.getPaymentById(id)
            .map(payment -> {
                model.addAttribute("payment", payment);
                try {
                    String orderId = razorpayService.createOrder(payment);
                    model.addAttribute("orderId", orderId);
                    model.addAttribute("keyId", keyId);
                    return "payment/pay";
                } catch (Exception ex) {
                    // Instead of "payment/error", redirect to the failure page
                    return "redirect:/payment/error";
                }
            })
            .orElse("redirect:/payments");
    }

    
    @PostMapping("/verify")
    @ResponseBody
    public ResponseEntity<String> verifyPayment(@RequestParam String razorpay_order_id,
                                               @RequestParam String razorpay_payment_id,
                                               @RequestParam String razorpay_signature) {
        try {
            boolean isValid = razorpayService.verifyPayment(razorpay_order_id, razorpay_payment_id, razorpay_signature);
            
            if (isValid) {
                // Find payment by order ID and update status
                Optional<MaintenancePayment> paymentOpt = paymentService.getPaymentById(Long.valueOf(razorpay_order_id.split("_")[1]));
                if (paymentOpt.isPresent()) {
                    paymentService.updatePaymentStatus(paymentOpt.get().getId(), PaymentStatus.PAID, 
                                                     razorpay_payment_id, razorpay_signature);
                    return ResponseEntity.ok("Payment verified successfully");
                }
            }
            return ResponseEntity.badRequest().body("Payment verification failed");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error verifying payment");
        }
    }
    
    @GetMapping("/success")
    public String paymentSuccess() {
        return "payment/success";
    }
    
    @GetMapping("/failure")
    public String paymentFailure() {
        return "payment/failure";
    }
    
    @GetMapping("/error")
    public String paymentError() {
        return "payment/error";
    }
    
    
}
