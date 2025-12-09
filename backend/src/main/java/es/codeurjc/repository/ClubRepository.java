package es.codeurjc.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.codeurjc.model.Club;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long>{
     @Query("""
        SELECT DISTINCT c FROM Club c
        LEFT JOIN c.sports s
        WHERE (:search IS NULL OR LOWER(c.name) LIKE :search)
          AND (:city IS NULL OR LOWER(c.city) = :city)
          AND (:sport IS NULL OR LOWER(s.name) = :sport)
    """)
    Page<Club> findFilteredClubs(Pageable pageable, @Param("search") String search, @Param("sport") String sport,@Param("city") String city);

    boolean existsBySportsId(long id);
}