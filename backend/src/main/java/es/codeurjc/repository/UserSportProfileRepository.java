package es.codeurjc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.model.Sport;
import es.codeurjc.model.UserSportProfile;

@Repository
public interface UserSportProfileRepository extends JpaRepository<UserSportProfile, Long> {
    List<UserSportProfile> findBySport(Sport sport);
}
