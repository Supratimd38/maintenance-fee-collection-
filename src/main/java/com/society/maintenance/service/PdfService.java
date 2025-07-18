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
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        BaseColor primaryColor = new BaseColor(25, 93, 169);   // Society blue
        BaseColor lightGray = new BaseColor(240, 242, 245);

        // Fonts
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, primaryColor);
        Font sectionHeader = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, primaryColor);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);

        // ====== Header ======
        Paragraph societyName = new Paragraph("SREE ENCLAVE RESIDENTIAL SOCIETY", headerFont);
        societyName.setAlignment(Element.ALIGN_LEFT);
        document.add(societyName);

        // Optional: logo image
        // Image logo = Image.getInstance("logo.png");
        // logo.scaleToFit(48, 48);
        // document.add(logo);

        Paragraph contactLine = new Paragraph("RAGHUNATHPUR, KOLKATA | Email: sreeenclave@gmail.com | Ph: 1234567890", new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, primaryColor));
        contactLine.setSpacingAfter(15f);
        document.add(contactLine);

        // ====== Status Stamp (PAID/PENDING/OVERDUE) ======
        String statusText = payment.getPaymentStatus().toString();
        BaseColor statusColor = statusText.equals("PAID") ? new BaseColor(41, 166, 45)
                             : statusText.equals("OVERDUE") ? BaseColor.RED : new BaseColor(255, 157, 0);
        Phrase statusPhrase = new Phrase(" " + statusText + " ", new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, BaseColor.WHITE));
        PdfPCell statusCell = new PdfPCell(statusPhrase);
        statusCell.setBackgroundColor(statusColor);
        statusCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        statusCell.setBorder(Rectangle.NO_BORDER);
        statusCell.setPadding(6f);
        statusCell.setPaddingRight(14f);
        PdfPTable statusTable = new PdfPTable(1);
        statusTable.setWidthPercentage(25);
        statusTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        statusTable.addCell(statusCell);
        statusTable.setSpacingAfter(10f);
        document.add(statusTable);

        // ====== Invoice Section ======
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setSpacingBefore(10f);
        detailsTable.setSpacingAfter(10f);
        detailsTable.setWidths(new float[]{2, 4});
        detailsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        // Owner Details Section Header
        PdfPCell ownerHeader = new PdfPCell(new Phrase("Owner Details", sectionHeader));
        ownerHeader.setColspan(2);
        ownerHeader.setBackgroundColor(primaryColor);
        ownerHeader.setPadding(7f);
        ownerHeader.setBorder(Rectangle.NO_BORDER);
        detailsTable.addCell(ownerHeader);

        addLabelValue(detailsTable, "Owner Name", payment.getFlatOwner().getName(), labelFont, valueFont);
        addLabelValue(detailsTable, "Flat Number", payment.getFlatOwner().getFlatNumber(), labelFont, valueFont);
        addLabelValue(detailsTable, "Wing", payment.getFlatOwner().getWing(), labelFont, valueFont);
        addLabelValue(detailsTable, "Floor", payment.getFlatOwner().getFloor().toString(), labelFont, valueFont);

        // Payment Details Section Header
        PdfPCell paymentHeader = new PdfPCell(new Phrase("Payment Details", sectionHeader));
        paymentHeader.setColspan(2);
        paymentHeader.setBackgroundColor(primaryColor);
        paymentHeader.setPadding(7f);
        paymentHeader.setBorder(Rectangle.NO_BORDER);
        paymentHeader.setPaddingTop(18f);
        detailsTable.addCell(paymentHeader);

        addLabelValue(detailsTable, "Payment Month", getMonthName(payment.getPaymentMonth()) + " " + payment.getPaymentYear(), labelFont, valueFont);
        addLabelValue(detailsTable, "Due Date", payment.getDueDate().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")), labelFont, valueFont);
        addLabelValue(detailsTable, "Amount (₹)", String.format("%.2f", payment.getAmount()), labelFont, valueFont);
        addLabelValue(detailsTable, "Status", payment.getPaymentStatus().toString(), labelFont, valueFont);

        // Info Box
        PdfPCell boxCell = new PdfPCell();
        boxCell.setColspan(2);
        boxCell.setBackgroundColor(lightGray);
        boxCell.setPadding(10f);
        Phrase box = new Phrase("Please pay by the due date to avoid late fees. For online payment, kindly refer to your society portal.", normalFont);
        boxCell.addElement(box);
        detailsTable.addCell(boxCell);

        document.add(detailsTable);

        // ====== Footer ======
        Paragraph thankYou = new Paragraph("Thank you for your prompt payment!", labelFont);
        thankYou.setAlignment(Element.ALIGN_CENTER);
        thankYou.setSpacingBefore(40f);
        document.add(thankYou);

        // Optionally add society council signature/image here

        document.close();
        return out.toByteArray();
    }

    // Helper function
    private void addLabelValue(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(6f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(6f);
        table.addCell(valueCell);
    }

    private String getMonthName(int month) {
        String[] months = {"", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
        return months[month];
    }

    
    }
