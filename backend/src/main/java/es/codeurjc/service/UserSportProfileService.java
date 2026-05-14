package es.codeurjc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.dto.UserSportProfileDTO;
import es.codeurjc.dto.UserSportProfileMapper;
import es.codeurjc.model.Match;
import es.codeurjc.model.Sport;
import es.codeurjc.model.User;
import es.codeurjc.model.UserSportProfile;
import es.codeurjc.repository.MatchRepository;
import es.codeurjc.repository.UserSportProfileRepository;
import jakarta.transaction.Transactional;

@Service
public class UserSportProfileService {
    
    @Autowired
    private UserSportProfileRepository userSportProfileRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserService userService;

    
    public UserSportProfileDTO save(UserSportProfile userSportProfile) {
        UserSportProfile savedUserSportProfile = userSportProfileRepository.save(userSportProfile);
        return UserSportProfileMapper.toDTO(savedUserSportProfile);
    }

    
    public List<UserSportProfile> findBySport(Sport sport) {
        return userSportProfileRepository.findBySport(sport);
    }


    @Transactional
    public void recalculateSportProfiles(Sport sport) {

        List<UserSportProfile> profiles = findBySport(sport);
        for (UserSportProfile p : profiles) {
            p.resetToInitial();
            save(p);
        }

        List<Match> matches = matchRepository.findBySportAndTypeTrueOrderByDateAsc(sport);
        matches = matches.stream()
                .filter(m -> m.getResult() != null && m.getResult().isCompleted())
                .toList();

        for (Match match : matches) {
            float sum1 = 0f, sum2 = 0f;

            for (User u : match.getTeam1Players()) {
                sum1 += u.getProfileForSport(sport).getLevel();
            }
            for (User u : match.getTeam2Players()) {
                sum2 += u.getProfileForSport(sport).getLevel();
            }

            float avg1 = sum1 / match.getTeam1Players().size();
            float avg2 = sum2 / match.getTeam2Players().size();

            for (User u : match.getTeam1Players()) {
                boolean won = match.didPlayerWin(u);
                u.applyMatchResult(match.getId(), sport, won, match.getDate(), avg1, avg2);
                save(u.getProfileForSport(sport));
                userService.update(u);
            }

            for (User u : match.getTeam2Players()) {
                boolean won = match.didPlayerWin(u);
                u.applyMatchResult(match.getId(), sport, won, match.getDate(), avg2, avg1);
                save(u.getProfileForSport(sport));
                userService.update(u);
            }
        }
    }
}
