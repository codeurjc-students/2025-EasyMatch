package es.codeurjc.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.codeurjc.model.Match;
import es.codeurjc.model.Sport;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long>{
     @Query("""
        SELECT m FROM Match m
        WHERE (:search IS NULL OR LOWER(m.club.name) LIKE :search)
          AND (:sport IS NULL OR LOWER(m.sport.name) LIKE :sport)
          AND (:includeFriendlies = TRUE OR m.type = TRUE)
          AND (
                :timeRange IS NULL OR
                (
                  CASE
                    WHEN :timeRange = 'morning' THEN FUNCTION('HOUR', FUNCTION('CONVERT_TZ', m.date, 'UTC', :timezone)) BETWEEN 6 AND 11
                    WHEN :timeRange = 'evening' THEN FUNCTION('HOUR', FUNCTION('CONVERT_TZ', m.date, 'UTC', :timezone)) BETWEEN 12 AND 17
                    WHEN :timeRange = 'night' THEN FUNCTION('HOUR', FUNCTION('CONVERT_TZ', m.date, 'UTC', :timezone)) BETWEEN 18 AND 23
                  END
                )
              )
          AND (:state IS NULL OR m.state = :state)
        ORDER BY m.date ASC
    """)
    Page<Match> findFilteredMatchesWithTimezone(
        Pageable pageable,
        @Param("search") String search,
        @Param("sport") String sport,
        @Param("includeFriendlies") Boolean includeFriendlies,
        @Param("timeRange") String timeRange,
        @Param("state") Boolean state,
        @Param("timezone") String timezone
    );

     @Query("""
        SELECT m FROM Match m
        WHERE (:search IS NULL OR LOWER(m.club.name) LIKE :search)
          AND (:sport IS NULL OR LOWER(m.sport.name) LIKE :sport)
          AND (:includeFriendlies = TRUE OR m.type = TRUE)
          AND (
                :timeRange IS NULL OR
                (
                  (:timeRange = 'morning' AND FUNCTION('HOUR', m.date) >= 6 AND FUNCTION('HOUR', m.date) < 12) OR
                  (:timeRange = 'evening' AND FUNCTION('HOUR', m.date) >= 12 AND FUNCTION('HOUR', m.date) < 18) OR
                  (:timeRange = 'night' AND FUNCTION('HOUR', m.date) >= 18 AND FUNCTION('HOUR', m.date) <= 23)
                )
              )
          AND (:state IS NULL OR m.state = :state)
        ORDER BY m.date ASC
    """)
    Page<Match> findFilteredMatchesH2(
        Pageable pageable,
        @Param("search") String search,
        @Param("sport") String sport,
        @Param("includeFriendlies") Boolean includeFriendlies,
        @Param("timeRange") String timeRange,
        @Param("state") Boolean state
    );

     List<Match> findByTeam1PlayersIdOrTeam2PlayersId(Long id, Long id2);
     
     List<Match> findBySportAndTypeTrueOrderByDateAsc(Sport sport);

     boolean existsBySportId(Long id);

}
