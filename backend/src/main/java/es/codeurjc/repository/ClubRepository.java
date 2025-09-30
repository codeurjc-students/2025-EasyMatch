package es.codeurjc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.model.Club;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long>{
    
}