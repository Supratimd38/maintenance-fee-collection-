package com.society.maintenance.controller;

import com.society.maintenance.model.FlatOwner;
import com.society.maintenance.model.MaintenancePayment;
import com.society.maintenance.service.FlatOwnerService;
import com.society.maintenance.service.PaymentService;
import com.society.maintenance.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {
    
    @Autowired
    private FlatOwnerService flatOwnerService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private PdfService pdfService;
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalFlatOwners", flatOwnerService.getTotalActiveFlatOwners());
        model.addAttribute("pendingPayments", paymentService.getPendingPaymentCount());
        model.addAttribute("paidPayments", paymentService.getPaidPaymentCount());
        model.addAttribute("overduePayments", paymentService.getOverduePayments().size());
        return "dashboard";
    }
    
    @GetMapping("/flat-owners")
    public String listFlatOwners(Model model) {
        List<FlatOwner> flatOwners = flatOwnerService.getAllActiveFlatOwners();
        model.addAttribute("flatOwners", flatOwners);
        return "flat-owners/list";
    }
    
    @GetMapping("/flat-owners/add")
    public String addFlatOwnerForm(Model model) {
        model.addAttribute("flatOwner", new FlatOwner());
        return "flat-owners/add";
    }
    
    @PostMapping("/flat-owners/add")
    public String addFlatOwner(@Valid @ModelAttribute FlatOwner flatOwner, 
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "flat-owners/add";
        }
        
        if (flatOwnerService.existsByEmail(flatOwner.getEmail())) {
            model.addAttribute("error", "Email already exists");
            return "flat-owners/add";
        }
        
        if (flatOwnerService.existsByFlatNumber(flatOwner.getFlatNumber())) {
            model.addAttribute("error", "Flat number already exists");
            return "flat-owners/add";
        }
        
        flatOwnerService.saveFlatOwner(flatOwner);
        return "redirect:/flat-owners";
    }
    
    @GetMapping("/flat-owners/edit/{id}")
    public String editFlatOwnerForm(@PathVariable Long id, Model model) {
        Optional<FlatOwner> flatOwner = flatOwnerService.getFlatOwnerById(id);
        if (flatOwner.isPresent()) {
            model.addAttribute("flatOwner", flatOwner.get());
            return "flat-owners/edit";
        }
        return "redirect:/flat-owners";
    }
    
    @PostMapping("/flat-owners/edit/{id}")
    public String editFlatOwner(@PathVariable Long id, @Valid @ModelAttribute FlatOwner flatOwner,
                               BindingResult result) {
        if (result.hasErrors()) {
            return "flat-owners/edit";
        }
        
        flatOwner.setId(id);
        flatOwnerService.updateFlatOwner(flatOwner);
        return "redirect:/flat-owners";
    }
    
    @GetMapping("/payments")
    public String listPayments(Model model) {
        List<MaintenancePayment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        return "payments/list";
    }
    
    @GetMapping("/payments/generate/{month}/{year}")
    public String generateMonthlyPayments(@PathVariable Integer month, @PathVariable Integer year) {
        paymentService.generateMonthlyPayments(month, year);
        return "redirect:/payments";
    }
    
    @GetMapping("/payments/bill/{id}")
    public ResponseEntity<byte[]> generateBill(@PathVariable Long id) {
        try {
            Optional<MaintenancePayment> paymentOpt = paymentService.getPaymentById(id);
            if (paymentOpt.isPresent()) {
                MaintenancePayment payment = paymentOpt.get();
                byte[] pdfBytes = pdfService.generateMaintenanceBill(payment);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", 
                    "bill_" + payment.getFlatOwner().getFlatNumber() + "_" + 
                    payment.getPaymentMonth() + "_" + payment.getPaymentYear() + ".pdf");
                
                return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        
        return ResponseEntity.notFound().build();
    }
}