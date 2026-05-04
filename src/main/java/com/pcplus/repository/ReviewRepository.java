package com.pcplus.repository;

import com.pcplus.model.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // this loads the review along with the related user and game
    // so the mvc page can safely read review.user.email without any crashing
    @EntityGraph(attributePaths = {"user", "game"})
    List<Review> findByGameIdAndRemovedFalse(Long gameId);

    // this checks whether or not the current user already reviewed the selected game or not
    Optional<Review> findByUserIdAndGameId(Long userId, Long gameId);

    // this is used to block duplicate reviews from the same user aka one review per game per user
    boolean existsByUserIdAndGameId(Long userId, Long gameId);

    // this supports publisher side review viewing if needed later
    @EntityGraph(attributePaths = {"user", "game"})
    List<Review> findByGameOwnerIdAndRemovedFalse(Long ownerId);
}