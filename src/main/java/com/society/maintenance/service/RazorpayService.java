package com.society.maintenance.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.society.maintenance.model.MaintenancePayment;
import com.society.maintenance.repository.MaintenancePaymentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

@Service
public class RazorpayService {
    
    @Autowired
    private RazorpayClient razorpayClient;
    
    @Autowired
    private MaintenancePaymentRepository paymentRepository;
    
    @Value("${razorpay.key.secret}")
    private String keySecret;
    
    @Value("${razorpay.currency}")
    private String currency;
    
    public String createOrder(MaintenancePayment payment) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", payment.getAmount() * 100); // Amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "payment_" + payment.getId());
        
        Order order = razorpayClient.orders.create(orderRequest);
        String orderId = order.get("id");
        
        // Update payment with order ID
        payment.setRazorpayOrderId(orderId);
        paymentRepository.save(payment);
        
        return orderId;
    }
    
    public boolean verifyPayment(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            String calculatedSignature = calculateSignature(payload, keySecret);
            return calculatedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
    
    private String calculateSignature(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] digest = mac.doFinal(payload.getBytes());
        return bytesToHex(digest);
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
