package com.paypilot.controller;

import com.paypilot.model.Category;
import com.paypilot.model.Frequency;
import com.paypilot.model.PaymentMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class EnumController {
    @GetMapping("/api/categories")
    public Category[] getCategories() {
        System.out.println("Categories fetched");
        return Category.values();
    }

    @GetMapping("/api/frequencies")
    public Frequency[] getFrequencies() {
        System.out.println("frequency fetched");
        return Frequency.values();
    }

    @GetMapping("/api/payment_methods")
    public PaymentMethod[] getPaymentMethods(){
        System.out.println("Payment methods fetched");
        return PaymentMethod.values();
    }

}
