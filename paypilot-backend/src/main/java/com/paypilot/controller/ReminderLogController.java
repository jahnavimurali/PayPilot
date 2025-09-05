package com.paypilot.controller;

import com.paypilot.model.ReminderLog;
import com.paypilot.repository.ReminderLogRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminder-logs")
@CrossOrigin(origins = "*")
public class ReminderLogController {

    private final ReminderLogRepository repository;

    public ReminderLogController(ReminderLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/debug")
    public String debug() {
        List<ReminderLog> all = repository.findAll();
        return "Found logs: " + all.size();
    }

    @GetMapping("/user/{userId}")
    public List<ReminderLog> getByUser(@PathVariable Long userId) {
        return repository.findByUserId(userId);
    }

    @GetMapping
    public List<ReminderLog> getAllLogs() {
        return repository.findAll();
    }
}