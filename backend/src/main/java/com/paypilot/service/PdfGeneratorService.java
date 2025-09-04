//package com.paypilot.service;
//
//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;
//import com.paypilot.model.Bill;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.Date;
//
//@Service
//public class PdfGeneratorService {
//
//    @Autowired
//    private UserService userService;
//
//    // Fonts
//    private final Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
//    private final Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
//    private final Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
//
//
//    // Helper for PDF cells
//    private PdfPCell getCell(String text, Font font, int alignment) {
//        PdfPCell cell = new PdfPCell(new Phrase(text, font));
//        cell.setPadding(8);
//        cell.setHorizontalAlignment(alignment);
//        cell.setBorderWidth(1);
//        return cell;
//    }
//
//    public ByteArrayOutputStream generatePdf(Bill bill) throws DocumentException, IOException {
//        // Bill Data
//        String billId = String.valueOf(bill.getId());
//        String billOwnerName = userService.getUserById(bill.getUserId()).getName();
//        String billGenerationDate = new Date().toString();
//        String billTitle = bill.getTitle();
//        String billCategory = bill.getCategory();
//        String billAmount = String.valueOf(bill.getAmount());
//        String billDueDate = bill.getDueDate().toString();
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
//
//        // Attach writer first
//        PdfWriter.getInstance(document, outputStream);
//        document.open();
//
//        // Title
//        Paragraph title = new Paragraph("BILL RECEIPT", titleFont);
//        title.setAlignment(Element.ALIGN_CENTER);
//        title.setSpacingAfter(20);
//        document.add(title);
//
//        // Metadata Table
//        PdfPTable metaTable = new PdfPTable(2);
//        metaTable.setWidthPercentage(100);
//        metaTable.setSpacingAfter(20);
//
//        metaTable.addCell(getCell("Bill ID:", headerFont, Element.ALIGN_LEFT));
//        metaTable.addCell(getCell(String.valueOf(bill.getId()), normalFont, Element.ALIGN_LEFT));
//
//        metaTable.addCell(getCell("Bearer's Name:", headerFont, Element.ALIGN_LEFT));
//        metaTable.addCell(getCell(userService.getUserById(bill.getUserId()).getName(), normalFont, Element.ALIGN_LEFT));
//
//        metaTable.addCell(getCell("Generated Date:", headerFont, Element.ALIGN_LEFT));
//        metaTable.addCell(getCell(new Date().toString(), normalFont, Element.ALIGN_LEFT));
//
//        document.add(metaTable);
//
//        // Bill Details Table
//        PdfPTable table = new PdfPTable(2);
//        table.setWidthPercentage(100);
//        table.setSpacingBefore(10);
//
//        table.addCell(getCell("Title", headerFont, Element.ALIGN_LEFT));
//        table.addCell(getCell(bill.getTitle(), normalFont, Element.ALIGN_LEFT));
//
//        table.addCell(getCell("Category", headerFont, Element.ALIGN_LEFT));
//        table.addCell(getCell(bill.getCategory(), normalFont, Element.ALIGN_LEFT));
//
//        table.addCell(getCell("Amount", headerFont, Element.ALIGN_LEFT));
//        table.addCell(getCell("₹" + bill.getAmount(), normalFont, Element.ALIGN_LEFT));
//
//        table.addCell(getCell("Due Date", headerFont, Element.ALIGN_LEFT));
//        table.addCell(getCell(bill.getDueDate().toString(), normalFont, Element.ALIGN_LEFT));
//
//        document.add(table);
//
//        // Footer
//        Paragraph footer = new Paragraph(
//                "This is a system-generated bill. No signature required.",
//                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC)
//        );
//        footer.setSpacingBefore(30);
//        footer.setAlignment(Element.ALIGN_CENTER);
//        document.add(footer);
//
//        document.close();
//        outputStream.close();
//
//        return outputStream;
//    }
//}

package com.paypilot.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.paypilot.model.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

@Service
public class PdfGeneratorService {

    @Autowired
    private UserService userService;

    // --- Helper methods ---
    private PdfPCell getColoredCell(String text, Font font, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorderWidth(0);
        return cell;
    }

    private PdfPCell getValueCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorderWidth(0.5f);
        return cell;
    }

    public ByteArrayOutputStream generatePdf(Bill bill) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        PdfWriter.getInstance(document, outputStream);
        document.open();

        // --- Brand Colors ---
        BaseColor primaryColor = new BaseColor(255, 102, 133);
        BaseColor accentColor = new BaseColor(255, 153, 128);

        // --- Fonts ---
        Font logoFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, accentColor);
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);

        // --- PayPilot Logo ---
        Paragraph logo = new Paragraph("PayPilot", logoFont);
        logo.setAlignment(Element.ALIGN_CENTER);
        logo.setSpacingAfter(10);
        document.add(logo);

        // --- Title ---
        Paragraph title = new Paragraph("BILL RECEIPT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // --- Metadata Table ---
        PdfPTable metaTable = new PdfPTable(2);
        metaTable.setWidthPercentage(100);
        metaTable.setSpacingAfter(20);

        metaTable.addCell(getColoredCell("Bill ID", headerFont, primaryColor));
        metaTable.addCell(getValueCell(String.valueOf(bill.getId()), normalFont));

        metaTable.addCell(getColoredCell("Bearer's Name", headerFont, primaryColor));
        metaTable.addCell(getValueCell(userService.getUserById(bill.getUserId()).getName(), normalFont));

        metaTable.addCell(getColoredCell("Generated Date", headerFont, primaryColor));
        metaTable.addCell(getValueCell(new Date().toString(), normalFont));

        document.add(metaTable);

        // --- Bill Details Table ---
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        table.addCell(getColoredCell("Title", headerFont, accentColor));
        table.addCell(getValueCell(bill.getTitle(), normalFont));

        table.addCell(getColoredCell("Category", headerFont, accentColor));
        table.addCell(getValueCell(String.valueOf(bill.getCategory()), normalFont));

        table.addCell(getColoredCell("Amount", headerFont, accentColor));
        table.addCell(getValueCell("₹" + bill.getAmount(), normalFont));

        table.addCell(getColoredCell("Due Date", headerFont, accentColor));
        table.addCell(getValueCell(bill.getDueDate().toString(), normalFont));

        document.add(table);

        // --- Footer ---
        Paragraph footer = new Paragraph(
                "This bill was generated via PayPilot – your smart billing partner.",
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY)
        );
        footer.setSpacingBefore(30);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        outputStream.close();

        return outputStream;
    }
}