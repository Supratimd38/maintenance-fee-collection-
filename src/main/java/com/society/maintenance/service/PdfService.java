package com.society.maintenance.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.society.maintenance.model.MaintenancePayment;
import com.society.maintenance.model.FlatOwner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {
    
    @Value("${razorpay.company.name}")
    private String companyName;
    
    public byte[] generateMaintenanceBill(MaintenancePayment payment) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Header
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);
            
            // Company Name
            Paragraph title = new Paragraph(companyName, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Bill Title
            Paragraph billTitle = new Paragraph("MAINTENANCE BILL", headerFont);
            billTitle.setAlignment(Element.ALIGN_CENTER);
            billTitle.setSpacingBefore(20);
            document.add(billTitle);
            
            // Bill Details Table
            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidthPercentage(100);
            detailsTable.setSpacingBefore(20);
            
            // Flat Owner Details
            FlatOwner flatOwner = payment.getFlatOwner();
            addTableRow(detailsTable, "Owner Name:", flatOwner.getName(), headerFont, normalFont);
            addTableRow(detailsTable, "Flat Number:", flatOwner.getFlatNumber(), headerFont, normalFont);
            addTableRow(detailsTable, "Wing:", flatOwner.getWing(), headerFont, normalFont);
            addTableRow(detailsTable, "Floor:", flatOwner.getFloor().toString(), headerFont, normalFont);
            
            // Payment Details
            addTableRow(detailsTable, "Payment Month:", 
                       getMonthName(payment.getPaymentMonth()) + " " + payment.getPaymentYear(), 
                       headerFont, normalFont);
            addTableRow(detailsTable, "Amount:", "₹" + payment.getAmount(), headerFont, normalFont);
            addTableRow(detailsTable, "Due Date:", 
                       payment.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                       headerFont, normalFont);
            addTableRow(detailsTable, "Status:", payment.getPaymentStatus().toString(), headerFont, normalFont);
            
            document.add(detailsTable);
            
            // Footer
            Paragraph footer = new Paragraph("Thank you for your prompt payment!", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);
            
            document.close();
            
        } catch (Exception e) {
            throw new DocumentException("Error generating PDF", e);
        }
        
        return out.toByteArray();
    }
    
    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }
    
    private String getMonthName(Integer month) {
        String[] months = {"", "January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"};
        return months[month];
    }
}
