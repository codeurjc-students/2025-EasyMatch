package es.codeurjc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.dto.UserSportProfileDTO;
import es.codeurjc.dto.UserSportProfileMapper;
import es.codeurjc.model.UserSportProfile;
import es.codeurjc.repository.UserSportProfileRepository;

@Service
public class UserSportProfileService {
    
    @Autowired
    private UserSportProfileRepository userSportProfileRepository;

    public UserSportProfileDTO save(UserSportProfile userSportProfile) {
        UserSportProfile savedUserSportProfile = userSportProfileRepository.save(userSportProfile);
        return UserSportProfileMapper.toDTO(savedUserSportProfile);
    }
}
