package es.codeurjc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.model.UserSportProfile;

@Repository
public interface UserSportProfileRepository extends JpaRepository<UserSportProfile, Long> {

}
