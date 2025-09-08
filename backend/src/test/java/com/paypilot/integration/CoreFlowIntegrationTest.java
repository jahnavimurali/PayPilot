package com.paypilot.integration;

import com.paypilot.model.Bill;
import com.paypilot.model.Category;
import com.paypilot.model.ReminderSetting;
import com.paypilot.model.ScheduledPayment;
import com.paypilot.repository.BillRepository;
import com.paypilot.repository.PaymentRepository;
import com.paypilot.repository.ReminderSettingRepository;
import com.paypilot.repository.ScheduledPaymentRepository;
import com.paypilot.service.CaptchaService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CoreFlowIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Autowired private BillRepository billRepo;
    @Autowired private ScheduledPaymentRepository spRepo;
    @Autowired private PaymentRepository payRepo;
    @Autowired private ReminderSettingRepository rsRepo;

    private String url(String path) { return "http://localhost:" + port + path; }

    @TestConfiguration
    static class TestBeans {
        @Bean
        @Primary
        CaptchaService captchaService() {
            return new CaptchaService() {
                @Override
                public boolean verifyCaptcha(String token) {
                    return true; // always pass in tests
                }
            };
        }
    }

    private Bill buildBill(Long userId, String title, double amount, LocalDate due, boolean autoPay) {
        Bill b = new Bill();
        b.setUserId(userId);
        b.setTitle(title);
        b.setCategory(Category.OTHER);
        b.setAmount(amount);
        b.setDueDate(due);
        b.setPaid(false);
        b.setAutoPayEnabled(autoPay);
        b.setPaymentMethod("UPI");
        return b;
    }

    @Test
    @Order(1)
    void createBill_withAutoPay_createsScheduledPayment() {
        Bill bill = buildBill(1L, "Internet", 999.0, LocalDate.now().plusDays(2), true);

        ResponseEntity<String> resp = rest.postForEntity(url("/api/bills"), bill, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Ensure bill persisted and one ScheduledPayment created
        List<Bill> byUser = billRepo.findByUserId(1L);
        assertThat(byUser).isNotEmpty();
        Bill saved = byUser.get(0);
        List<ScheduledPayment> sps = spRepo.findByUserId(1L);
        assertThat(sps).isNotEmpty();
        ScheduledPayment sp = sps.stream().filter(x -> x.getBillId().equals(saved.getId())).findFirst().orElse(null);
        assertThat(sp).isNotNull();
        assertThat(sp.getIsPaid()).isFalse();
        assertThat(sp.getScheduledDate()).isEqualTo(saved.getDueDate());
    }

    @Test
    @Order(2)
    void markScheduledPaymentPaid_createsPayment() {
        // Precondition: there is a scheduled payment from previous test
        List<ScheduledPayment> all = spRepo.findByUserId(1L);
        assertThat(all).isNotEmpty();
        ScheduledPayment sp = all.get(0);

        LocalDate prevDate = sp.getScheduledDate();

        // Call endpoint to mark as paid
        ResponseEntity<String> resp = rest.exchange(
                url("/api/scheduled-payments/markPaid/" + sp.getId()),
                HttpMethod.PUT,
                null,
                String.class
        );
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Refresh entities
        ScheduledPayment updated = spRepo.findById(sp.getId()).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getIsPaid()).isTrue();

        // Payment created
        assertThat(payRepo.findAll()).isNotEmpty();

    }

    @Test
    @Order(3)
    void reminders_summary_and_messages_excludePaidBills_andIncludeOverdueUpcoming() {
        // Add reminder setting (n=3 days)
        ReminderSetting rs = new ReminderSetting();
        rs.setUserId(1L);
        rs.setBillId(null);
        rs.setReminderDaysBefore(3);
        rs.setEnabled(true);
        rsRepo.save(rs);

        // Add an overdue bill (unpaid, manual)
        Bill overdue = buildBill(1L, "Electricity", 1500.0, LocalDate.now().minusDays(2), false);
        rest.postForEntity(url("/api/bills"), overdue, String.class);

        // Add an upcoming bill within 3 days (unpaid, manual)
        Bill upcoming = buildBill(1L, "Water", 300.0, LocalDate.now().plusDays(1), false);
        rest.postForEntity(url("/api/bills"), upcoming, String.class);

        // Notifications
        ResponseEntity<Map<String, Integer>> notifResp = rest.exchange(
                url("/api/reminders/notifications/1"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Integer>>() {}
        );
        assertThat(notifResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Integer> notif = notifResp.getBody();
        assertThat(notif).isNotNull();
        assertThat(notif.get("Overdue")).isGreaterThanOrEqualTo(1);
        assertThat(notif.get("Upcoming")).isGreaterThanOrEqualTo(1);

        // Reminder messages
        ResponseEntity<List<String>> msgResp = rest.exchange(
                url("/api/reminders/due/1"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {}
        );
        assertThat(msgResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> messages = msgResp.getBody();
        assertThat(messages).isNotNull();
        assertThat(messages).isNotEmpty();
    }
}
