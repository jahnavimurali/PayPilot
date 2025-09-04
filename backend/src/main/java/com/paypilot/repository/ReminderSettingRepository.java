package com.paypilot.repository;

import com.paypilot.model.ReminderSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReminderSettingRepository extends JpaRepository<ReminderSetting, Long> {
    List<ReminderSetting> findByUserIdOrderByIdDesc(Long userId);

    @Query("DELETE FROM ReminderSetting rs WHERE rs.billId = :billId")
    void deleteReminderSettingByBillId(Long billId);
}
