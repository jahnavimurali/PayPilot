package com.paypilot.service;

import com.paypilot.model.Payment;
import com.paypilot.model.ScheduledPayment;
import com.paypilot.repository.PaymentRepository;
import com.paypilot.repository.ScheduledPaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ScheduledPaymentRepository scheduledPaymentRepository;

    private PaymentService paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        // manually wire service with mocks to ensure correct instances are used
        paymentService = new PaymentService(paymentRepository);
        try {
            java.lang.reflect.Field f = PaymentService.class.getDeclaredField("scheduledPaymentRepository");
            f.setAccessible(true);
            f.set(paymentService, scheduledPaymentRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        payment = new Payment();
        payment.setId(1L);
        payment.setUserId(10L);
        payment.setBillId(100L);
        payment.setAmount(50.00);
        payment.setScheduledDate(LocalDate.now());
    }

    @Test
    void addPayment_MarksRelatedScheduledPaymentsPaid() {
        ScheduledPayment spMatch = new ScheduledPayment(10L, 100L, 50.00, LocalDate.now(), "CARD");
        spMatch.setId(200L);
        spMatch.setIsPaid(false);

        ScheduledPayment spOther = new ScheduledPayment(10L, 999L, 10.00, LocalDate.now(), "CARD");
        spOther.setId(201L);
        spOther.setIsPaid(false);

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(scheduledPaymentRepository.findByUserId(10L)).thenReturn(Arrays.asList(spMatch, spOther));

        paymentService.addPayment(payment);
        // focus on side-effects and repository interactions
        verify(paymentRepository).save(any(Payment.class));
        assertTrue(spMatch.getIsPaid());
        assertFalse(spOther.getIsPaid());
        verify(scheduledPaymentRepository).save(spMatch);
        verify(scheduledPaymentRepository, never()).save(spOther);
    }

    @Test
    void getAllAndByUserId() {
        when(paymentRepository.findAll()).thenReturn(Collections.singletonList(payment));
        when(paymentRepository.findByUserIdOrderByScheduledDateAsc(10L)).thenReturn(Collections.singletonList(payment));
        assertEquals(1, paymentService.getAllPayments().size());
        assertEquals(1, paymentService.getPaymentsByUserId(10L).size());
    }

    @Test
    void updatePayment_UpdatesFields() {
        Payment updates = new Payment();
        updates.setAmount(75.00);
        updates.setMethod("UPI");
        updates.setScheduledDate(LocalDate.now().plusDays(1));

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment updated = paymentService.updatePayment(1L, updates);
        assertEquals(75.00, updated.getAmount());
        assertEquals("UPI", updated.getMethod());
    }

    @Test
    void deletePayment_ThrowsWhenNotExists() {
        lenient().when(paymentRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> paymentService.deletePayment(1L));
    }
}
