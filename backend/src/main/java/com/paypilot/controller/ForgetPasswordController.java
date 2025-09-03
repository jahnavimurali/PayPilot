package com.paypilot.controller;

import com.paypilot.service.ForgetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ForgetPasswordController {

    @Autowired
    private ForgetPasswordService forgetPasswordService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> body) {
        try{
            forgetPasswordService.generateAndSendOTP(body.get("email"));
            return ResponseEntity.ok("OTP sent to email");
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("User Not Found!");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> body) {
        try{
            forgetPasswordService.verifyOtpAndReset(
                    body.get("email"),
                    body.get("otp"),
                    body.get("newPassword")
            );
            return ResponseEntity.ok("Password reset successful");
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest().body("Invalid Credentials");
        }
    }
}

