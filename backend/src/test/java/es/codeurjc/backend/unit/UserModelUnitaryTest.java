package es.codeurjc.backend.unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import es.codeurjc.model.LevelHistory;
import es.codeurjc.model.Match;
import es.codeurjc.model.User;

@Tag("unit")
@ActiveProfiles("test")
public class UserModelUnitaryTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                "Ramón Pérez",
                "ramon69",
                "ramon@emeal.com",
                "ramon132",
                LocalDateTime.now(),
                true,
                "Soy Ramón",
                4.5f,
                "USER"
        );
    }

    @Test
    void verifyLevelIncreasesWhenWinningAgainstStrongerOpponent() {
        float initialLevel = user.getLevel();

        user.applyMatchResult(
                true,
                LocalDateTime.now(),
                3.5f, // own team avg 
                5.5f  // opponent team avg (stronger)
        );
        
        // Level increase calculation:
        // 1 / 1 + 10 ^ ((5.5 - 3.5) / 1.25 ) = 0.0245 
        // 0.2 * (1 - 0.0245) = 0.1951
        // 4.5 + 0.1951 = 4.6951

        double roundedLevel = Math.round(user.getLevel() * 10000.0) / 10000.0;
        assertTrue(user.getLevel() > initialLevel);
        assertThat(roundedLevel, is(4.6951));
    }

    @Test
    void verifyLevelIncreasesSlightlyWhenWinningAgainstWeakerOpponent() {
        float initialLevel = user.getLevel();

        user.applyMatchResult(
                true,
                LocalDateTime.now(),
                5.5f, // own team avg 
                3.5f // opponent team avg (weaker)
        );

        // Level increase calculation:
        // 1 / 1 + 10 ^ ((3.5 - 5.5) / 1.25 ) = 0.9755 
        // 0.2 * (1 - 0.9755) = 0.0049
        // 4.5 + 0.0049 = 4.5049

        double roundedLevel = Math.round(user.getLevel() * 10000.0) / 10000.0;
        assertTrue(user.getLevel() > initialLevel);
        assertThat(roundedLevel, is(4.5049));
    }

    @Test
    void verifyLevelDecreasesWhenLosing() {
        float initialLevel = user.getLevel();

        user.applyMatchResult(
                false,
                LocalDateTime.now(),
                4.0f,
                4.0f
        );

        // Level decrease calculation:
        // 1 / 1 + 10 ^ ((4.0 - 4.0) / 1.25 ) = 0.5 
        // 0.2 * (0 - 0.5) = -0.1
        // 4.5 + (-0.1) = 4.4

        assertTrue(user.getLevel() < initialLevel);
        assertThat(user.getLevel(), is(4.4f));
    }

    @Test
    void verifyClampLevelToMinimum() {
        user.setLevel(1.0f);

        user.applyMatchResult(
                false,
                LocalDateTime.now(),
                1.0f,
                7.0f
        );

        assertEquals(1.0f, user.getLevel());
    }

    @Test
    void verifyClampLevelToMaximum() {
        user.setLevel(7.0f);

        user.applyMatchResult(
                true,
                LocalDateTime.now(),
                7.0f,
                1.0f
        );

        assertEquals(7.0f, user.getLevel());
    }

    @Test
    void verifyEntryIsAddedToLevelHistory() {
        int initialSize = user.getLevelHistory().size();

        user.applyMatchResult(
                true,
                LocalDateTime.now(),
                4.0f,
                4.0f
        );

        assertEquals(initialSize + 1, user.getLevelHistory().size());
    }

    @Test
    void levelHistoryShouldContainCorrectData() {
        LocalDateTime date = LocalDateTime.now();
        float previousLevel = user.getLevel();

        user.applyMatchResult(true, date, 4.0f, 4.0f);

        LevelHistory entry = user.getLevelHistory().get(0);

        assertEquals(previousLevel, entry.getLevelBefore());
        assertEquals(user.getLevel(), entry.getLevelAfter());
        assertTrue(entry.isWon());
        assertEquals(date, entry.getDate());
    }

    @Test
    void verifyGetMatchHistoryCombinesBothTeams() {
        Match m1 = new Match();
        Match m2 = new Match();

        user.setMatchesAsTeam1Player(java.util.List.of(m1));
        user.setMatchesAsTeam2Player(java.util.List.of(m2));

        assertEquals(2, user.getMatchHistory().size());
    }
}
