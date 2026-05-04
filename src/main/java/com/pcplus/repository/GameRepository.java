package com.pcplus.repository;

import com.pcplus.model.Game;
import com.pcplus.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByStatusOrderByDownloadCountDesc(String status);

    List<Game> findByOwner(User owner);

    @Query("SELECT g FROM Game g WHERE g.status = 'live' AND " +
           "(LOWER(g.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           " LOWER(g.genres) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Game> search(@Param("q") String query, Pageable pageable);

    @Query("SELECT g FROM Game g WHERE g.status = 'live' ORDER BY g.downloadCount DESC")
    List<Game> findTopSellers(Pageable pageable);

    @Query("SELECT g FROM Game g WHERE g.status = 'live' ORDER BY g.createdAt DESC")
    List<Game> findNewest(Pageable pageable);

    @Query("SELECT g FROM Game g WHERE g.status = 'live' AND g.salePrice IS NOT NULL")
    List<Game> findOnSale();
}
