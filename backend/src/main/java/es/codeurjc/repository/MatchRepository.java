package es.codeurjc.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.codeurjc.model.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long>{
     @Query("""
        SELECT m FROM Match m
        WHERE (:search IS NULL OR LOWER(m.club.name) LIKE :search)
          AND (:sport IS NULL OR LOWER(m.sport.name) LIKE :sport)
          AND (:includeFriendlies = TRUE OR m.type = TRUE)
          AND (
                :timeRange IS NULL OR
                (:timeRange = 'morning' AND FUNCTION('HOUR', m.date) >= 5 AND FUNCTION('HOUR', m.date) < 11) OR
                (:timeRange = 'evening' AND FUNCTION('HOUR', m.date) >= 11 AND FUNCTION('HOUR', m.date) < 17) OR
                (:timeRange = 'night' AND FUNCTION('HOUR', m.date) >= 17 AND FUNCTION('HOUR', m.date) <= 22)
              )
          AND (:state IS NULL OR m.state = :state)
        ORDER BY m.date ASC
    """)
    Page<Match> findFilteredMatches(Pageable pageable, @Param("search") String search, @Param("sport") String sport,
            @Param("includeFriendlies") Boolean includeFriendlies,
            @Param("timeRange") String timeRange, @Param("state") Boolean state
    );

     List<Match> findByTeam1PlayersIdOrTeam2PlayersId(Long id, Long id2);

}
