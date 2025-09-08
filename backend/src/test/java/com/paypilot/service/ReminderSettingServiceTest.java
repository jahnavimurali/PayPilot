package com.paypilot.service;

import com.paypilot.model.ReminderSetting;
import com.paypilot.repository.ReminderSettingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReminderSettingServiceTest {

    @Mock
    private ReminderSettingRepository repo;

    @InjectMocks
    private ReminderSettingService service;

    @Test
    void addAndGetAndDelete() {
        ReminderSetting rs = new ReminderSetting();
        when(repo.save(any(ReminderSetting.class))).thenReturn(rs);
        when(repo.findAll()).thenReturn(Collections.singletonList(rs));
        when(repo.findByUserIdOrderByIdDesc(1L)).thenReturn(Collections.singletonList(rs));

        ReminderSetting saved = service.add(rs);
        assertNotNull(saved);
        assertEquals(1, service.getAll().size());
        assertEquals(1, service.getByUserId(1L).size());
        service.delete(1L);
        verify(repo).deleteById(1L);
    }
}
