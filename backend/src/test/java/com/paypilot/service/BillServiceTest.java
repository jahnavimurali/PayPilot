package com.paypilot.service;

import com.paypilot.model.Bill;
import com.paypilot.repository.BillRepository;
import com.paypilot.repository.PaymentRepository;
import com.paypilot.repository.ScheduledPaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock
    private BillRepository billRepository;
    @Mock
    private ScheduledPaymentRepository scheduledPaymentRepository;
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private BillService billService;

    private Bill bill;

    @BeforeEach
    void setUp() {
        bill = new Bill();
        bill.setId(1L);
        bill.setUserId(10L);
        bill.setTitle("Test Bill");
        bill.setAmount(100.00);
        bill.setDueDate(LocalDate.now());
        bill.setPaid(false);
    }

    @Test
    void addBill_Success() {
        when(billRepository.save(any(Bill.class))).thenReturn(bill);
        Bill savedBill = billService.addBill(bill);
        assertNotNull(savedBill);
        assertEquals(bill.getTitle(), savedBill.getTitle());
    }

    @Test
    void getBillById_SuccessAndNotFound() {
        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));
        assertNotNull(billService.getBillById(1L));
        when(billRepository.findById(2L)).thenReturn(Optional.empty());
        assertNull(billService.getBillById(2L));
    }

    @Test
    void updateBill_Success() {
        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));
        when(billRepository.save(any(Bill.class))).thenReturn(bill);
        bill.setTitle("Updated Test Bill");
        Bill updatedBill = billService.updateBill(bill);
        assertNotNull(updatedBill);
        assertEquals("Updated Test Bill", updatedBill.getTitle());
    }

    @Test
    void payBill_SetsPaidTrue() {
        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));
        billService.payBill(bill);
        assertTrue(bill.getIsPaid());
        verify(billRepository).save(bill);
    }

    @Test
    void deleteBillById_DeletesDependentsThenBill() {
        billService.deleteBillById(1L);
        verify(paymentRepository).deleteByBillId(1L);
        verify(scheduledPaymentRepository).deleteByBillId(1L);
        verify(billRepository).deleteById(1L);
    }
}
