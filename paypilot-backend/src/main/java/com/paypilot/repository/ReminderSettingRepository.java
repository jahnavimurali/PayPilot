package com.paypilot.repository;

import com.paypilot.model.ReminderSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReminderSettingRepository extends JpaRepository<ReminderSetting, Long> {
    List<ReminderSetting> findByUserIdOrderByIdDesc(Long userId);
}