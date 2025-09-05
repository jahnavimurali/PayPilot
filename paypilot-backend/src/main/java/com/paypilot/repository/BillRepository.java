package com.paypilot.repository;

import com.paypilot.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {

    @Query("SELECT b FROM Bill b WHERE TRIM(UPPER(b.category)) = TRIM(UPPER(:category))")
    List<Bill> findByCategoryIgnoreCase(@Param("category") String category);

    @Query("SELECT b FROM Bill b WHERE b.userId = :userId")
    List<Bill> findByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM Bill b WHERE b.userId = :userId AND TRIM(UPPER(b.category)) = TRIM(UPPER(:category))")
    List<Bill> findByUserIdAndCategory(@Param("userId") Long userId,
                                       @Param("category") String category);

    // Optional: only needed if your BillController insists on "deleteBillByComposite"
    // Assumes Bill has fields: id, userId, paymentMethod, amount
    @Modifying
    @Transactional
    int deleteByIdAndUserIdAndPaymentMethodAndAmount(Long id,
                                                     Long userId,
                                                     String paymentMethod,
                                                     Double amount);
}
