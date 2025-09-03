package com.paypilot.scheduler;

import com.paypilot.model.Bill;
import com.paypilot.model.ReminderLog;
import com.paypilot.model.ReminderSetting;
import com.paypilot.repository.BillRepository;
import com.paypilot.repository.ReminderLogRepository;
import com.paypilot.repository.ReminderSettingRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReminderScheduler {

    private final ReminderSettingRepository reminderRepo;
    private final BillRepository billRepo;
    private final ReminderLogRepository reminderLogRepo;

    public ReminderScheduler(ReminderSettingRepository reminderRepo,
                             BillRepository billRepo,
                             ReminderLogRepository reminderLogRepo) {
        this.reminderRepo = reminderRepo;
        this.billRepo = billRepo;
        this.reminderLogRepo = reminderLogRepo;
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ ReminderScheduler bean loaded.");
        runOnceAtStart();
    }

    public void runOnceAtStart() {
        sendReminders();
    }

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void sendReminders() {
        System.out.println("⏰ Running reminder check at: " + LocalDateTime.now());

        LocalDate today = LocalDate.now();
        List<ReminderSetting> allSettings = reminderRepo.findAll();

        for (ReminderSetting setting : allSettings) {
            Long billId = setting.getBillId();

            if (billId == null) {
                System.out.println("⚠️ Skipping setting with NULL billId for userId: " + setting.getUserId());
                continue;
            }

            Bill bill = billRepo.findById(billId).orElse(null);
            if (bill == null) {
                System.out.println("⚠️ Bill not found for ID: " + billId);
                continue;
            }

            LocalDate dueDate = bill.getDueDate();
            int daysBefore = setting.getReminderDaysBefore();
            LocalDate reminderDate = dueDate.minusDays(daysBefore);

            if (today.equals(reminderDate) && Boolean.TRUE.equals(setting.getEnabled())) {
                String msg = "🔔 Reminder: Bill '" + bill.getTitle() + "' is due in " +
                        daysBefore + " day(s) (Due: " + dueDate + ")";
                System.out.println("✅ Triggered: " + msg);

                ReminderLog log = new ReminderLog();
                log.setUserId(setting.getUserId());
                log.setBillId(bill.getId());
                log.setMessage(msg);
                log.setLogDate(LocalDateTime.now());

                reminderLogRepo.save(log);
            }
        }
    }
}
