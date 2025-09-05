package com.paypilot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.paypilot.model.RecaptchaResponse;

import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class CaptchaService {

    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    @Value("${google.recaptcha.verify.url}")
    private String recaptchaVerifyUrl;

    public boolean verifyCaptcha(String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            URI verifyUri = new URI(recaptchaVerifyUrl + "?secret=" + recaptchaSecret + "&response=" + token);
            ResponseEntity<RecaptchaResponse> response = restTemplate.postForEntity(verifyUri, null, RecaptchaResponse.class);
            return response.getBody() != null && response.getBody().isSuccess();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }
}