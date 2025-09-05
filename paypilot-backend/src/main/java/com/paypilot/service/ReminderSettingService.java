package com.paypilot.service;

import com.paypilot.model.ReminderSetting;
import com.paypilot.repository.ReminderSettingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReminderSettingService {

    private final ReminderSettingRepository repo;

    public ReminderSettingService(ReminderSettingRepository repo) {
        this.repo = repo;
    }

    public ReminderSetting add(ReminderSetting r) {
        return repo.save(r);
    }

    public List<ReminderSetting> getAll() {
        return repo.findAll();
    }

    // Use the latest (most recent) settings first
    public List<ReminderSetting> getByUserId(Long userId) {
        return repo.findByUserIdOrderByIdDesc(userId);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}