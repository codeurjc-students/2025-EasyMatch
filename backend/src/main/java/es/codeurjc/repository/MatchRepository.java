package es.codeurjc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.model.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long>{
}
