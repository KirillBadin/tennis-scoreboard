package io.github.kirillbadin.tennisscoreboard.live;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MatchScore")
class MatchScoreTest {
    private static final int GAMES_TO_WIN_SET = 6;
    private static final int POINTS_TO_WIN_GAME = 4;
    private static final int SETS_TO_WIN_MATCH = 2;

    private MatchScore match;

    @BeforeEach
    void setUp() {
        match = new MatchScore();
    }

    @Test
    @DisplayName("initial state: 0-0 sets, not finished")
    void initialState() {
        assertEquals(0, match.getSets(PlayerSide.FIRST));
        assertEquals(0, match.getSets(PlayerSide.SECOND));
        assertFalse(match.isFinished());
        assertTrue(match.getWinner().isEmpty());
    }

    @Nested
    @DisplayName("set progression")
    class SetProgression {

        @Test
        @DisplayName("FIRST wins first set, 1-0")
        void firstWinsFirstSet() {
            winSetFor(PlayerSide.FIRST);
            assertEquals(1, match.getSets(PlayerSide.FIRST));
            assertEquals(0, match.getSets(PlayerSide.SECOND));
            assertFalse(match.isFinished());
        }

        @Test
        @DisplayName("both sides win one set, 1-1")
        void oneEach() {
            winSetFor(PlayerSide.FIRST);
            winSetFor(PlayerSide.SECOND);
            assertEquals(1, match.getSets(PlayerSide.FIRST));
            assertEquals(1, match.getSets(PlayerSide.SECOND));
            assertFalse(match.isFinished());
        }

        @Test
        @DisplayName("current set does not count into sets score")
        void currentSetNotCounted() {
            winGamesFor(PlayerSide.FIRST, GAMES_TO_WIN_SET - 1);
            assertEquals(0, match.getSets(PlayerSide.FIRST));
        }
    }

    @Nested
    @DisplayName("match finishing")
    class MatchFinishing {

        @Test
        @DisplayName("FIRST wins 2-0")
        void firstWins2_0() {
            winSetFor(PlayerSide.FIRST);
            winSetFor(PlayerSide.FIRST);
            assertTrue(match.isFinished());
            assertEquals(SETS_TO_WIN_MATCH, match.getSets(PlayerSide.FIRST));
            assertEquals(PlayerSide.FIRST, match.getWinner().orElse(null));
        }

        @Test
        @DisplayName("SECOND wins 2-0")
        void secondWins2_0() {
            winSetFor(PlayerSide.SECOND);
            winSetFor(PlayerSide.SECOND);
            assertTrue(match.isFinished());
            assertEquals(SETS_TO_WIN_MATCH, match.getSets(PlayerSide.SECOND));
            assertEquals(PlayerSide.SECOND, match.getWinner().orElse(null));
        }

        @Test
        @DisplayName("FIRST wins 2-1")
        void firstWins2_1() {
            winSetFor(PlayerSide.SECOND);
            winSetFor(PlayerSide.FIRST);
            winSetFor(PlayerSide.FIRST);
            assertTrue(match.isFinished());
            assertEquals(2, match.getSets(PlayerSide.FIRST));
            assertEquals(1, match.getSets(PlayerSide.SECOND));
            assertEquals(PlayerSide.FIRST, match.getWinner().orElse(null));
        }

        @Test
        @DisplayName("SECOND wins 2-1")
        void secondWins2_1() {
            winSetFor(PlayerSide.FIRST);
            winSetFor(PlayerSide.SECOND);
            winSetFor(PlayerSide.SECOND);
            assertTrue(match.isFinished());
            assertEquals(1, match.getSets(PlayerSide.FIRST));
            assertEquals(2, match.getSets(PlayerSide.SECOND));
            assertEquals(PlayerSide.SECOND, match.getWinner().orElse(null));
        }
    }

    @Nested
    @DisplayName("exception handling")
    class ExceptionHandling {

        @Test
        @DisplayName("addPoint after match finished throws")
        void throwsAfterFinish() {
            winSetFor(PlayerSide.FIRST);
            winSetFor(PlayerSide.FIRST);
            assertThrows(IllegalStateException.class, () -> match.addPoint(PlayerSide.FIRST));
        }
    }

    private void score(PlayerSide side, int count) {
        for (int i = 0; i < count; i++) {
            match.addPoint(side);
        }
    }

    private void winGameFor(PlayerSide side) {
        score(side, POINTS_TO_WIN_GAME);
    }

    private void winGamesFor(PlayerSide side, int games) {
        for (int i = 0; i < games; i++) {
            winGameFor(side);
        }
    }

    private void winSetFor(PlayerSide side) {
        winGamesFor(side, GAMES_TO_WIN_SET);
    }
}