package com.pcplus.repository;

import com.pcplus.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByGameIdAndRemovedFalse(Long gameId);
    Optional<Review> findByUserIdAndGameId(Long userId, Long gameId);
    boolean existsByUserIdAndGameId(Long userId, Long gameId);
    List<Review> findByGameOwnerIdAndRemovedFalse(Long ownerId);
}
