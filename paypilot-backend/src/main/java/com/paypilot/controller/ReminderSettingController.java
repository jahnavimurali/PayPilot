package com.paypilot.controller;

import com.paypilot.model.Bill;
import com.paypilot.model.ReminderSetting;
import com.paypilot.model.ScheduledPayment;
import com.paypilot.repository.ReminderSettingRepository;
import com.paypilot.service.BillService;
import com.paypilot.service.ReminderSettingService;
import com.paypilot.service.ScheduledPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reminders")
@CrossOrigin(origins = "*")
public class ReminderSettingController {

    @Autowired
    private ReminderSettingService service;

    @Autowired
    private ReminderSettingRepository reminderSettingRepository;

    @Autowired
    private ScheduledPaymentService scheduledPaymentService;

    @Autowired
    private BillService billService;

    @PostMapping
    public ReminderSetting add(@RequestBody ReminderSetting setting) {
        return service.add(setting);
    }

    @GetMapping
    public List<ReminderSetting> getAll() {
        return service.getAll();
    }

    // Always return latest settings first
    @GetMapping("/user/{userId}")
    public List<ReminderSetting> getByUser(@PathVariable Long userId) {
        return reminderSettingRepository.findByUserIdOrderByIdDesc(userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ========= CORE CHANGE: use ALL BILLS as source of truth =========
    // GET /api/reminders/due/{userId}
    // Returns reminder messages for every UNPAID bill that is overdue or within N days.
    @GetMapping("/due/{userId}")
    public List<String> getUpcomingReminders(@PathVariable Long userId) {
        int n = reminderSettingRepository.findByUserIdOrderByIdDesc(userId)
                .stream()
                .findFirst()
                .map(ReminderSetting::getReminderDaysBefore)
                .orElse(2);

        LocalDate today = LocalDate.now();
        List<String> reminders = new ArrayList<>();

        // 1) All bills for this user
        List<Bill> bills = billService.getAllBillsByUserId(userId);

        // 2) Paid map from scheduled payments (only to know PAID state)
        Set<Long> paidBillIds = scheduledPaymentService.getPaymentsByUserId(userId)
                .stream()
                .filter(sp -> Boolean.TRUE.equals(sp.getIsPaid()))
                .map(ScheduledPayment::getBillId)
                .collect(Collectors.toSet());

        // 3) Build reminders for unpaid bills
        for (Bill bill : bills) {
            if (bill == null || bill.getDueDate() == null) continue;
            if (paidBillIds.contains(bill.getId())) continue; // skip already paid

            LocalDate due = bill.getDueDate();
            long daysLeft = ChronoUnit.DAYS.between(today, due);

            if (daysLeft < 0) {
                reminders.add(String.format(
                        "⚠️ Your %s (Amount: ₹%.2f) was due on %s and is still unpaid!",
                        safe(bill.getTitle()), bill.getAmount(), due
                ));
            } else if (daysLeft <= n) {
                reminders.add(String.format(
                        "You have %d day%s left to pay your %s (Amount: ₹%.2f, Due: %s)",
                        daysLeft, (daysLeft == 1 ? "" : "s"),
                        safe(bill.getTitle()), bill.getAmount(), due
                ));
            }
        }

        return reminders;
    }

    // GET /api/reminders/notifications/{userId}
    // Returns counts { Overdue, Upcoming } for UNPAID bills only (regardless of scheduling)
    @GetMapping("/notifications/{userId}")
    public Map<String, Integer> getNotificationSummary(@PathVariable Long userId) {
        int n = reminderSettingRepository.findByUserIdOrderByIdDesc(userId)
                .stream()
                .findFirst()
                .map(ReminderSetting::getReminderDaysBefore)
                .orElse(2);

        LocalDate today = LocalDate.now();

        List<Bill> bills = billService.getAllBillsByUserId(userId);
        Set<Long> paidBillIds = scheduledPaymentService.getPaymentsByUserId(userId)
                .stream()
                .filter(sp -> Boolean.TRUE.equals(sp.getIsPaid()))
                .map(ScheduledPayment::getBillId)
                .collect(Collectors.toSet());

        int upcoming = 0;
        int overdue = 0;

        for (Bill bill : bills) {
            if (bill == null || bill.getDueDate() == null) continue;
            if (paidBillIds.contains(bill.getId())) continue;

            long daysLeft = ChronoUnit.DAYS.between(today, bill.getDueDate());
            if (daysLeft < 0) overdue++;
            else if (daysLeft <= n) upcoming++;
        }

        Map<String, Integer> notification = new HashMap<>();
        notification.put("Overdue", overdue);
        notification.put("Upcoming", upcoming);
        return notification;
    }

    // small helper to avoid NPEs in formatting
    private String safe(String s) {
        return (s == null) ? "bill" : s;
    }
}
