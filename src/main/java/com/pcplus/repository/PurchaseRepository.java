package com.pcplus.repository;

import com.pcplus.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUserId(Long userId);
    boolean existsByUserIdAndGameId(Long userId, Long gameId);

    long countByGameId(Long gameId);

    @Query("SELECT COALESCE(SUM(p.pricePaid), 0) FROM Purchase p WHERE p.game.id = :gameId")
    BigDecimal sumRevenueByGameId(@Param("gameId") Long gameId);

    @Query("SELECT COALESCE(SUM(p.pricePaid), 0) FROM Purchase p WHERE p.game.owner.id = :ownerId")
    BigDecimal sumRevenueByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.game.owner.id = :ownerId")
    long countSalesByOwnerId(@Param("ownerId") Long ownerId);
}
