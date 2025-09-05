package com.paypilot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paypilot.model.ReminderLog;

public interface ReminderLogRepository extends JpaRepository<ReminderLog, Long> {
    List<ReminderLog> findByUserId(Long userId);
}