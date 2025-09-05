package com.paypilot.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column
    private String name;
    @Column
    private String panCardNumber;
    @Column
    private Long bankAccountNumber;
    @Column
    private String ifscCode;
    @Column
    private String bankingPartner;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPanCardNumber() {
        return panCardNumber;
    }
    public void setPanCardNumber(String panCardNumber) {
        this.panCardNumber = panCardNumber;
    }
    public Long getBankAccountNumber() {
        return bankAccountNumber;
    }
    public void setBankAccountNumber(Long bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }
    public String getIfscCode() {
        return ifscCode;
    }
    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }
    public String getBankingPartner() {
        return bankingPartner;
    }
    public void setBankingPartner(String bankingPartner) {
        this.bankingPartner = bankingPartner;
    }



}