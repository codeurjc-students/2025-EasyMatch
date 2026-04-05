package es.codeurjc.backend.unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import es.codeurjc.model.LevelHistory;
import es.codeurjc.model.Mode;
import es.codeurjc.model.ScoringType;
import es.codeurjc.model.Sport;
import es.codeurjc.model.User;
import es.codeurjc.model.UserSportProfile;

@Tag("unit")
@ActiveProfiles("test")
public class UserSportProfileUnitaryTest {

    private User user;
    private Sport tennis;
    private UserSportProfile tennisLevel;

    @BeforeEach
    void setUp() {
        user = new User(
                "Ramón Pérez",
                "ramon69",
                "ramon@emeal.com",
                "ramon132",
                LocalDateTime.now(),
                true,
                "Soy Ramón"
        );

        tennis = new Sport("Tennis", List.of(new Mode("Singles", 2)), ScoringType.SETS);
        tennisLevel = new UserSportProfile(user, tennis, 4.5f);
        user.getSportProfiles().add(tennisLevel);
    }

    @Test
    void verifyLevelIncreasesWhenWinningAgainstStrongerOpponent() {
        float initialLevel = tennisLevel.getLevel();

        tennisLevel.applyMatchResult(true, false, LocalDateTime.now(), 3.5f, 5.5f);

        // Level increase calculation:
        // 1 / 1 + 10 ^ ((5.5 - 3.5) / 1.25 ) = 0.0245 
        // 0.2 * (1 - 0.0245) = 0.1951
        // 4.5 + 0.1951 = 4.6951

        double roundedLevel = Math.round(tennisLevel.getLevel() * 10000.0) / 10000.0;
        assertTrue(tennisLevel.getLevel() > initialLevel);
        assertThat(roundedLevel, is(4.6951));
    }

    @Test
    void verifyLevelIncreasesSlightlyWhenWinningAgainstWeakerOpponent() {
        float initialLevel = tennisLevel.getLevel();

        tennisLevel.applyMatchResult(true, false, LocalDateTime.now(), 5.5f, 3.5f);

        // Level increase calculation:
        // 1 / 1 + 10 ^ ((3.5 - 5.5) / 1.25 ) = 0.9755 
        // 0.2 * (1 - 0.9755) = 0.0049
        // 4.5 + 0.0049 = 4.5049

        double roundedLevel = Math.round(tennisLevel.getLevel() * 10000.0) / 10000.0;
        assertTrue(tennisLevel.getLevel() > initialLevel);
        assertThat(roundedLevel, is(4.5049));
    }

    @Test
    void verifyLevelDecreasesWhenLosing() {
        float initialLevel = tennisLevel.getLevel();

        tennisLevel.applyMatchResult(false, false, LocalDateTime.now(), 4.0f, 4.0f);

        // Level decrease calculation:
        // 1 / 1 + 10 ^ ((4.0 - 4.0) / 1.25 ) = 0.5 
        // 0.2 * (0 - 0.5) = -0.1
        // 4.5 + (-0.1) = 4.4

        assertTrue(tennisLevel.getLevel() < initialLevel);
        assertThat(tennisLevel.getLevel(), is(4.4f));
    }

    @Test
    void verifyClampLevelToMinimum() {
        tennisLevel.setLevel(1.0f);
        tennisLevel.applyMatchResult(false, false, LocalDateTime.now(), 1.0f, 7.0f);

        assertEquals(1.0f, tennisLevel.getLevel());
    }

    @Test
    void verifyClampLevelToMaximum() {
        tennisLevel.setLevel(7.0f);
        tennisLevel.applyMatchResult(true, false, LocalDateTime.now(), 7.0f, 1.0f);

        assertEquals(7.0f, tennisLevel.getLevel());
    }

    @Test
    void verifyEntryIsAddedToLevelHistory() {
        int initialSize = tennisLevel.getLevelHistory().size();

        tennisLevel.applyMatchResult(true, false, LocalDateTime.now(), 4.0f, 4.0f);

        assertEquals(initialSize + 1, tennisLevel.getLevelHistory().size());
    }

    @Test
    void levelHistoryShouldContainCorrectData() {
        LocalDateTime date = LocalDateTime.now();
        float previousLevel = tennisLevel.getLevel();

        tennisLevel.applyMatchResult(true, false, date, 4.0f, 4.0f);

        LevelHistory entry = tennisLevel.getLevelHistory().get(0);

        assertEquals(previousLevel, entry.getLevelBefore());
        assertEquals(tennisLevel.getLevel(), entry.getLevelAfter());
        assertTrue(entry.isWon());
        assertEquals(date, entry.getDate());
    }
}
