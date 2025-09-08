package com.paypilot.service;

import com.paypilot.model.Bill;
import com.paypilot.model.Payment;
import com.paypilot.model.ScheduledPayment;
import com.paypilot.repository.ScheduledPaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledPaymentServiceTest {

    @Mock
    private ScheduledPaymentRepository scheduledPaymentRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private BillService billService;

    @InjectMocks
    private ScheduledPaymentService scheduledPaymentService;

    private Bill bill;

    @BeforeEach
    void setUp() {
        bill = new Bill();
        bill.setId(100L);
        bill.setUserId(10L);
        bill.setAmount(120.00);
        bill.setDueDate(LocalDate.now());
        bill.setPaymentMethod("CARD");
    }

    @Test
    void createScheduledPaymentFromBill_AutoPayEnabled_Schedules() {
        bill.setAutoPayEnabled(true);
        scheduledPaymentService.createScheduledPaymentFromBill(bill);
        verify(scheduledPaymentRepository).save(any(ScheduledPayment.class));
    }

    @Test
    void setScheduledPaymentStatusPaid_Once_MarksPaidAndReturns() {
        ScheduledPayment sp = new ScheduledPayment(10L, 100L, 120.00, LocalDate.now(), "CARD");
        sp.setId(1L);
        sp.setIsPaid(false);

        when(scheduledPaymentRepository.findById(1L)).thenReturn(Optional.of(sp));
        when(billService.getBillById(100L)).thenReturn(bill);

        scheduledPaymentService.setScheduledPaymentStatusPaid(1L);

        verify(paymentService).addPayment(any(Payment.class));
        assertTrue(sp.getIsPaid());
        verify(scheduledPaymentRepository).save(sp);
        verify(scheduledPaymentRepository, never()).save(argThat(x -> x.getId() == null && !x.getIsPaid()));
    }
}
