package com.paypilot.service;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;

class CaptchaServiceTest {

    @Test
    void verifyCaptcha_ReturnsFalse_OnBadUrl() throws Exception {
        CaptchaService service = new CaptchaService();
        // set fields via reflection
        Field urlField = CaptchaService.class.getDeclaredField("recaptchaVerifyUrl");
        urlField.setAccessible(true);
        urlField.set(service, "http://bad url"); // will trigger URISyntaxException

        Field secretField = CaptchaService.class.getDeclaredField("recaptchaSecret");
        secretField.setAccessible(true);
        secretField.set(service, "secret");

        boolean result = service.verifyCaptcha("token");
        assertFalse(result);
    }
}
