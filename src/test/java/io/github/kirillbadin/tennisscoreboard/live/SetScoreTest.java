package io.github.kirillbadin.tennisscoreboard.live;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SetScore")
class SetScoreTest {
    private static final int GAMES_TO_WIN = 6;
    private static final int TIEBREAK_START_GAMES = 6;
    private static final int POINTS_TO_WIN_GAME = 4;

    private SetScore set;

    @BeforeEach
    void setUp() {
        set = new SetScore();
    }

    @Test
    @DisplayName("initial state: 0-0, not finished, no tiebreak")
    void initialState() {
        assertEquals(0, set.getGames(PlayerSide.FIRST));
        assertEquals(0, set.getGames(PlayerSide.SECOND));
        assertFalse(set.isFinished());
        assertTrue(set.getWinner().isEmpty());
        assertEquals(GamePoint.LOVE, set.getGamePoint(PlayerSide.FIRST));
        assertNull(set.getTieBreakPoints(PlayerSide.FIRST));
    }

    @Nested
    @DisplayName("game progression")
    class GameProgression {

        @Test
        @DisplayName("FIRST wins a game, score 1-0")
        void firstWinsGame() {
            winGameFor(PlayerSide.FIRST);
            assertEquals(1, set.getGames(PlayerSide.FIRST));
            assertEquals(0, set.getGames(PlayerSide.SECOND));
            assertFalse(set.isFinished());
        }

        @Test
        @DisplayName("both sides win games, score 2-1")
        void bothSidesWin() {
            winGamesFor(PlayerSide.FIRST, 2);
            winGamesFor(PlayerSide.SECOND, 1);
            assertEquals(2, set.getGames(PlayerSide.FIRST));
            assertEquals(1, set.getGames(PlayerSide.SECOND));
        }

        @Test
        @DisplayName("game points progress within a game")
        void gamePointsProgress() {
            set.addPoint(PlayerSide.FIRST);
            assertEquals(GamePoint.FIFTEEN, set.getGamePoint(PlayerSide.FIRST));
            set.addPoint(PlayerSide.FIRST);
            assertEquals(GamePoint.THIRTY, set.getGamePoint(PlayerSide.FIRST));
            set.addPoint(PlayerSide.FIRST);
            assertEquals(GamePoint.FORTY, set.getGamePoint(PlayerSide.FIRST));
        }
    }

    @Nested
    @DisplayName("set finishing")
    class SetFinishing {

        @Test
        @DisplayName("FIRST wins 6-0")
        void firstWins6_0() {
            winGamesFor(PlayerSide.FIRST, GAMES_TO_WIN);
            assertTrue(set.isFinished());
            assertEquals(PlayerSide.FIRST, set.getWinner().orElse(null));
        }

        @Test
        @DisplayName("SECOND wins 6-4")
        void secondWins6_4() {
            winGamesFor(PlayerSide.FIRST, 4);
            winGamesFor(PlayerSide.SECOND, 6);
            assertTrue(set.isFinished());
            assertEquals(6, set.getGames(PlayerSide.SECOND));
            assertEquals(4, set.getGames(PlayerSide.FIRST));
            assertEquals(PlayerSide.SECOND, set.getWinner().orElse(null));
        }

        @Test
        @DisplayName("5-5 is not finished")
        void notFinished5_5() {
            winGamesFor(PlayerSide.FIRST, 5);
            winGamesFor(PlayerSide.SECOND, 5);
            assertFalse(set.isFinished());
        }

        @Test
        @DisplayName("6-5 is not finished (need 2-game lead)")
        void notFinished6_5() {
            winGamesFor(PlayerSide.FIRST, 5);
            winGamesFor(PlayerSide.SECOND, 5);
            winGameFor(PlayerSide.FIRST);
            assertEquals(6, set.getGames(PlayerSide.FIRST));
            assertEquals(5, set.getGames(PlayerSide.SECOND));
            assertFalse(set.isFinished());
        }

        @Test
        @DisplayName("FIRST wins 7-5 after 5-5")
        void firstWins7_5() {
            winGamesFor(PlayerSide.FIRST, 5);
            winGamesFor(PlayerSide.SECOND, 5);
            winGamesFor(PlayerSide.FIRST, 2);
            assertTrue(set.isFinished());
            assertEquals(7, set.getGames(PlayerSide.FIRST));
            assertEquals(5, set.getGames(PlayerSide.SECOND));
            assertEquals(PlayerSide.FIRST, set.getWinner().orElse(null));
        }
    }

    @Nested
    @DisplayName("tiebreak scenarios")
    class TiebreakScenarios {

        @BeforeEach
        void reachTiebreak() {
            for (int i = 0; i < TIEBREAK_START_GAMES; i++) {
                winGameFor(PlayerSide.FIRST);
                winGameFor(PlayerSide.SECOND);
            }
        }

        @Test
        @DisplayName("6-6 activates tiebreak, set not finished")
        void tiebreakActive() {
            assertFalse(set.isFinished());
            assertTrue(set.getWinner().isEmpty());
            assertNotNull(set.getTieBreakPoints(PlayerSide.FIRST));
            assertEquals(0, set.getTieBreakPoints(PlayerSide.FIRST));
            assertEquals(0, set.getTieBreakPoints(PlayerSide.SECOND));
        }

        @Test
        @DisplayName("during tiebreak winner is empty")
        void winnerEmptyDuringTiebreak() {
            score(PlayerSide.FIRST, 3);
            score(PlayerSide.SECOND, 2);
            assertTrue(set.getWinner().isEmpty());
        }

        @Test
        @DisplayName("FIRST wins set 7-6 via tiebreak 7-0")
        void firstWins7_6() {
            score(PlayerSide.FIRST, 7);
            assertTrue(set.isFinished());
            assertEquals(7, set.getGames(PlayerSide.FIRST));
            assertEquals(6, set.getGames(PlayerSide.SECOND));
            assertEquals(PlayerSide.FIRST, set.getWinner().orElse(null));
        }

        @Test
        @DisplayName("SECOND wins extended tiebreak 8-6")
        void secondWinsExtended() {
            score(PlayerSide.FIRST, 6);
            score(PlayerSide.SECOND, 6);
            score(PlayerSide.SECOND, 2);
            assertTrue(set.isFinished());
            assertEquals(7, set.getGames(PlayerSide.SECOND));
            assertEquals(6, set.getGames(PlayerSide.FIRST));
            assertEquals(PlayerSide.SECOND, set.getWinner().orElse(null));
        }

        @Test
        @DisplayName("tiebreak points are null after set finished")
        void tieBreakPointsNullAfterFinish() {
            score(PlayerSide.FIRST, 7);
            assertNull(set.getTieBreakPoints(PlayerSide.FIRST));
            assertNull(set.getTieBreakPoints(PlayerSide.SECOND));
        }
    }

    @Nested
    @DisplayName("exception handling")
    class ExceptionHandling {

        @Test
        @DisplayName("addPoint after set finished throws")
        void throwsAfterFinish() {
            winGamesFor(PlayerSide.FIRST, GAMES_TO_WIN);
            assertThrows(IllegalStateException.class, () -> set.addPoint(PlayerSide.FIRST));
        }

        @Test
        @DisplayName("addPoint after tiebreak finish throws")
        void throwsAfterTiebreakFinish() {
            for (int i = 0; i < TIEBREAK_START_GAMES; i++) {
                winGameFor(PlayerSide.FIRST);
                winGameFor(PlayerSide.SECOND);
            }
            score(PlayerSide.FIRST, 7);
            assertThrows(IllegalStateException.class, () -> set.addPoint(PlayerSide.FIRST));
        }
    }

    private void score(PlayerSide side, int count) {
        for (int i = 0; i < count; i++) {
            set.addPoint(side);
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
}