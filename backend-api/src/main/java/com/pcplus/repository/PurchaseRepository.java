package com.pcplus.repository;

import com.pcplus.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUserId(Long userId);
    boolean existsByUserIdAndGameId(Long userId, Long gameId);
}
