package com.paypilot.controller;

import com.paypilot.model.Bill;
import com.paypilot.service.BillService;
import com.paypilot.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itextpdf.text.DocumentException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @PostMapping
    public ResponseEntity<Bill> addBill(@RequestBody Bill bill) {
        return ResponseEntity.ok(billService.addBill(bill));
    }

    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        return ResponseEntity.ok(billService.getAllBills());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Bill>> getBillById(@PathVariable long userId) {
        return ResponseEntity.ok(billService.getAllBillsByUserId(userId));
    }

    @GetMapping("/{userId}/{category}")
    public ResponseEntity<List<Bill>> getBillsByCategory(@PathVariable long userId, @PathVariable String category) {
        return ResponseEntity.ok(billService.getBillsByUserIdAndCategory(userId, category));
    }

    @GetMapping("/bycategory/{category}")
    public List<Bill> getBillsByCategory(@PathVariable String category) {
        System.out.println("Incoming category param: '" + category + "'");
        return billService.getBillsByCategory(category);
    }

    //  Get bills by userId + category + date range
    @GetMapping("/filter")
    public List<Bill> filterBills(
            @RequestParam Long userId,
            @RequestParam String category,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return billService.getBillsByUserIdAndCategoryAndDateRange(userId, category, start, end);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> generatePdf(@PathVariable Long id) throws DocumentException, IOException {
        try{
            Bill bill = billService.getBillById(id);
            ByteArrayOutputStream pdfStream = pdfGeneratorService.generatePdf(bill);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "generated.pdf");
            return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
        }catch (Exception e){
            System.out.println("Unable to fetch request!");
//            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }


}
