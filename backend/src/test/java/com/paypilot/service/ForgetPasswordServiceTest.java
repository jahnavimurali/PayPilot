package com.paypilot.service;

import com.paypilot.model.PasswordResetToken;
import com.paypilot.model.User;
import com.paypilot.repository.TokenRepository;
import com.paypilot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForgetPasswordServiceTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private TokenRepository tokenRepo;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ForgetPasswordService service;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(10L);
        user.setEmail("user@example.com");
    }

    @Test
    void generateAndSendOTP_SendsEmail() {
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        service.generateAndSendOTP("user@example.com");
        verify(tokenRepo).save(any(PasswordResetToken.class));
        verify(emailService).send(eq("user@example.com"), any(), contains("OTP"));
    }

    @Test
    void verifyOtpAndReset_HappyPath() throws Exception {
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        PasswordResetToken latest = new PasswordResetToken();
        latest.setUserId(10L);
        latest.setOTP("123456");
        latest.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        when(tokenRepo.findByUserIdOrderByExpiryDateDesc(10L))
                .thenReturn(Collections.singletonList(latest));
        when(passwordEncoder.encode("newpass")).thenReturn("enc");

        // set private currEmail via reflection to simulate previous OTP generation flow
        Field f = ForgetPasswordService.class.getDeclaredField("currEmail");
        f.setAccessible(true);
        f.set(service, "user@example.com");

        service.verifyOtpAndReset("user@example.com", "123456", "newpass");
        verify(userRepo).save(any(User.class));
    }

    @Test
    void verifyOtpAndReset_InvalidOtp_Throws() throws Exception {
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        PasswordResetToken latest = new PasswordResetToken();
        latest.setUserId(10L);
        latest.setOTP("000000");
        latest.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        when(tokenRepo.findByUserIdOrderByExpiryDateDesc(10L))
                .thenReturn(Collections.singletonList(latest));

        Field f = ForgetPasswordService.class.getDeclaredField("currEmail");
        f.setAccessible(true);
        f.set(service, "user@example.com");

        assertThrows(RuntimeException.class,
                () -> service.verifyOtpAndReset("user@example.com", "123456", "newpass"));
    }
}
