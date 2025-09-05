package com.paypilot.controller;

import com.itextpdf.text.DocumentException;
import com.paypilot.model.Bill;
import com.paypilot.model.ScheduledPayment;
import com.paypilot.service.BillService;
import com.paypilot.service.PdfGeneratorService;
import com.paypilot.service.ScheduledPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private ScheduledPaymentService scheduledPaymentService;

    @PostMapping
    public ResponseEntity<?> addBill(@RequestBody Bill bill) {
        Bill newBill = new Bill(bill.getTitle(), bill.getCategory(), bill.getAmount(), bill.getDueDate(),
                bill.getUserId(),bill.isRecurring(), bill.getFrequency(), bill.isPaid(), bill.isSnoozeReminders(),
                bill.isAutoPayEnabled(), bill.getPaymentMethod());
        billService.addBill(newBill);
        try {
            scheduledPaymentService.createScheduledPaymentFromBill(newBill);
        } catch (RuntimeException e) {
            return ResponseEntity.ok("Bill Added Successfully");
        }
        return ResponseEntity.ok("Bill added successfully");
    }

    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        return ResponseEntity.ok(billService.getAllBills());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Bill>> getBillById(@PathVariable Long userId) {
        return ResponseEntity.ok(billService.getAllBillsByUserId(userId));
    }

    @GetMapping("/{userId}/{category}")
    public ResponseEntity<List<Bill>> getBillsByCategory(@PathVariable Long userId, @PathVariable String category) {
        return ResponseEntity.ok(billService.getBillsByUserIdAndCategory(userId, category));
    }

    @GetMapping("/bycategory/{category}")
    public List<Bill> getBillsByCategory(@PathVariable String category) {
        System.out.println("Incoming category param: '" + category + "'");
        return billService.getBillsByCategory(category);
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

    @PutMapping("/update")
    public ResponseEntity<?> updateBill(@RequestBody Bill bill){
        try{
            billService.updateBill(bill);
            return ResponseEntity.ok("Bill Details Updated Successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Bill Details Not Updated :" + e.getMessage());
        }
    }

//    @DeleteMapping("/delete/{billId}")
//    public ResponseEntity<?> deleteBill(@PathVariable Long billId) {
//        try {
//            Bill bill = billService.getBillById(billId);
//            bill.setAutoPayEnabled(false);
//            billService.addBill(bill);
//            return ResponseEntity.ok("Bill deleted successfully");
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body("Bill not found!");
//        }
//    }
}