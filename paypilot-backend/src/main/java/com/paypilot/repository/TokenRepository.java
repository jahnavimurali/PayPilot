package com.paypilot.repository;

import com.paypilot.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<PasswordResetToken, Long> {
    List<PasswordResetToken> findByUserIdOrderByExpiryDateDesc(long id);
}
