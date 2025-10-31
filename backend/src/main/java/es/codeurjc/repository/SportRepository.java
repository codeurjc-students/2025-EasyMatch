package es.codeurjc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.model.Sport;

@Repository
public interface SportRepository extends JpaRepository<Sport, Long>{
    
}
