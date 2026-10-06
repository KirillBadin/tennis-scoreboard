package io.github.kirillbadin.tennisscoreboard.repositories;

import io.github.kirillbadin.tennisscoreboard.entity.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m " +
            "WHERE LOWER(m.firstPlayer.name) LIKE LOWER(CONCAT('%', :playerName, '%')) " +
            "OR LOWER(m.secondPlayer.name) LIKE LOWER(CONCAT('%', :playerName, '%')) ")
    Page<Match> findAllByPlayerName(@Param("playerName") String playerName, Pageable pageable);
}
