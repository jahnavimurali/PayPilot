package com.paypilot.service;

import com.paypilot.model.PasswordResetToken;
import com.paypilot.model.User;
import com.paypilot.repository.TokenRepository;
import com.paypilot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ForgetPasswordService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TokenRepository tokenRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String currEmail;

    public void generateAndSendOTP(String email) {
        Optional<User> user = userRepo.findByEmail(email);
        if (user==null) {
            throw new RuntimeException("User not found");
        }
        User currUser = user.get();
        currEmail = currUser.getEmail();

        // 6-digit OTP
        int otpInt = (int)(Math.random() * 900000) + 100000;
        String otp = String.valueOf(otpInt);

        PasswordResetToken record = new PasswordResetToken();
        record.setUserId(currUser.getId());
        record.setOTP(otp);
        record.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        tokenRepo.save(record);

        String text = "Your OTP for password reset is: " + otp +
                "\nIt will expire in 10 minutes.";
        emailService.send(email, "Password Reset OTP", text);
    }

    public void verifyOtpAndReset(String email, String otp, String newPassword) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User currUser = userOpt.get();
        System.out.println(currUser.getId());

        List<PasswordResetToken> record = tokenRepo.findByUserIdOrderByExpiryDateDesc(currUser.getId());
        if (record.isEmpty()) {
            throw new RuntimeException("No OTP found");
        }

        PasswordResetToken latest = record.get(0);

        if (!latest.getOTP().equals(otp) || latest.isExpired() || !currUser.getEmail().equals(currEmail)) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        currUser.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(currUser);

    }
}

