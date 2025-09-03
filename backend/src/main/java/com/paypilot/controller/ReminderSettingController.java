package com.paypilot.controller;

import com.paypilot.model.ReminderSetting;
import com.paypilot.model.Bill;
import com.paypilot.service.ReminderSettingService;
import com.paypilot.repository.BillRepository;
import com.paypilot.repository.ReminderSettingRepository;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/reminders")
public class ReminderSettingController {

    private final ReminderSettingService service;
    private final ReminderSettingRepository reminderSettingRepository;
    private final BillRepository billRepository;

    public ReminderSettingController(
            ReminderSettingService service,
            ReminderSettingRepository reminderSettingRepository,
            BillRepository billRepository
    ) {
        this.service = service;
        this.reminderSettingRepository = reminderSettingRepository;
        this.billRepository = billRepository;
    }

    @PostMapping
    public ReminderSetting add(@RequestBody ReminderSetting setting) {
        return service.add(setting);
    }

    @GetMapping
    public List<ReminderSetting> getAll() {
        return service.getAll();
    }

    // FIXED: Always return latest settings first
    @GetMapping("/user/{userId}")
    public List<ReminderSetting> getByUser(@PathVariable Long userId) {
        return reminderSettingRepository.findByUserIdOrderByIdDesc(userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // GET /api/reminders/due/{userId}
    @GetMapping("/due/{userId}")
    public List<String> getUpcomingReminders(@PathVariable Long userId) {
        // Always use the latest setting (newest row)
        List<ReminderSetting> settings = reminderSettingRepository.findByUserIdOrderByIdDesc(userId);
        int n = (settings == null || settings.isEmpty())
                ? 2
                : settings.get(0).getReminderDaysBefore(); // 0 is latest

        LocalDate today = LocalDate.now();

        List<Bill> bills = billRepository.findByUserId(userId);
        List<String> reminders = new ArrayList<>();

        for (Bill bill : bills) {
            if (bill.getDueDate() == null) continue;
            LocalDate due = bill.getDueDate(); // Bill#getDueDate() should return LocalDate
            long daysLeft = ChronoUnit.DAYS.between(today, due);

            // Show reminder if due in exactly n, 1, or 0 days
            if (daysLeft == n || daysLeft == 1 || daysLeft == 0) {
                String msg = String.format(
                        "You have %d day%s left to pay your %s (Amount: ₹%.2f, Due: %s)",
                        daysLeft,
                        daysLeft == 1 ? "" : "s",
                        bill.getTitle(),
                        bill.getAmount(),
                        due
                );
                reminders.add(msg);
            }
        }
        return reminders;
    }
}
