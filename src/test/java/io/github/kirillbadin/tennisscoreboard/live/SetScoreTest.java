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
    @DisplayName("newSet: 0-0 по геймам, сет не завершён, тайбрейка нет")
    void newSet_hasZeroGamesAndNoWinner() {
        assertEquals(0, set.getGames(PlayerSide.FIRST));
        assertEquals(0, set.getGames(PlayerSide.SECOND));
        assertFalse(set.isFinished());
        assertTrue(set.getWinner().isEmpty());
        assertEquals(GamePoint.LOVE, set.getGamePoint(PlayerSide.FIRST));
        assertNull(set.getTieBreakPoints(PlayerSide.FIRST));
    }

    @Nested
    @DisplayName("addPoint: прогрессия геймов")
    class AddPointGameProgression {

        @Test
        @DisplayName("addPoint: FIRST выигрывает гейм → 1-0, сет не завершён")
        void addPoint_whenFirstWinsGame_incrementsFirstGames() {
            winGameFor(PlayerSide.FIRST);

            assertEquals(1, set.getGames(PlayerSide.FIRST));
            assertEquals(0, set.getGames(PlayerSide.SECOND));
            assertFalse(set.isFinished());
        }

        @Test
        @DisplayName("addPoint: обе стороны берут геймы → 2-1")
        void addPoint_whenBothSidesWin_gamesCountSeparately() {
            winGamesFor(PlayerSide.FIRST, 2);
            winGamesFor(PlayerSide.SECOND, 1);

            assertEquals(2, set.getGames(PlayerSide.FIRST));
            assertEquals(1, set.getGames(PlayerSide.SECOND));
        }

        @Test
        @DisplayName("addPoint: очки внутри гейма растут FIFTEEN → THIRTY → FORTY")
        void addPoint_pointsProgressToForty() {
            set.addPoint(PlayerSide.FIRST);
            assertEquals(GamePoint.FIFTEEN, set.getGamePoint(PlayerSide.FIRST));

            set.addPoint(PlayerSide.FIRST);
            assertEquals(GamePoint.THIRTY, set.getGamePoint(PlayerSide.FIRST));

            set.addPoint(PlayerSide.FIRST);
            assertEquals(GamePoint.FORTY, set.getGamePoint(PlayerSide.FIRST));
        }
    }

    @Nested
    @DisplayName("getWinner: завершение сета")
    class GetWinnerFinishing {

        @Test
        @DisplayName("getWinner: FIRST выигрывает 6-0 → возвращает FIRST")
        void getWinner_whenFirstWinsSixToZero_returnsFirst() {
            winGamesFor(PlayerSide.FIRST, GAMES_TO_WIN);

            assertTrue(set.isFinished());
            assertEquals(PlayerSide.FIRST, set.getWinner().orElse(null));
        }

        @Test
        @DisplayName("getWinner: SECOND выигрывает 6-4 → возвращает SECOND")
        void getWinner_whenSecondWinsSixToFour_returnsSecond() {
            winGamesFor(PlayerSide.FIRST, 4);
            winGamesFor(PlayerSide.SECOND, 6);

            assertTrue(set.isFinished());
            assertEquals(6, set.getGames(PlayerSide.SECOND));
            assertEquals(4, set.getGames(PlayerSide.FIRST));
            assertEquals(PlayerSide.SECOND, set.getWinner().orElse(null));
        }

        @Test
        @DisplayName("getWinner: при 5-5 → empty")
        void getWinner_atFiveFive_returnsEmpty() {
            winGamesFor(PlayerSide.FIRST, 5);
            winGamesFor(PlayerSide.SECOND, 5);

            assertFalse(set.isFinished());
        }

        @Test
        @DisplayName("getWinner: при 6-5 → empty (нужен отрыв в 2 гейма)")
        void getWinner_atSixFive_returnsEmpty() {
            winGamesFor(PlayerSide.FIRST, 5);
            winGamesFor(PlayerSide.SECOND, 5);
            winGameFor(PlayerSide.FIRST);

            assertEquals(6, set.getGames(PlayerSide.FIRST));
            assertEquals(5, set.getGames(PlayerSide.SECOND));
            assertFalse(set.isFinished());
        }

        @Test
        @DisplayName("getWinner: после 5-5 FIRST берёт два гейма подряд → 7-5, возвращает FIRST")
        void getWinner_afterFiveFiveAndTwoConsecutiveGames_returnsFirst() {
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
    @DisplayName("addPoint: сценарии тайбрейка")
    class AddPointTieBreakScenarios {

        @BeforeEach
        void reachTiebreak() {
            for (int i = 0; i < TIEBREAK_START_GAMES; i++) {
                winGameFor(PlayerSide.FIRST);
                winGameFor(PlayerSide.SECOND);
            }
        }

        @Test
        @DisplayName("atSixSix: тайбрейк активен, сет не завершён, очки 0-0")
        void atSixSix_tiebreakIsActive() {
            assertFalse(set.isFinished());
            assertTrue(set.getWinner().isEmpty());
            assertNotNull(set.getTieBreakPoints(PlayerSide.FIRST));
            assertEquals(0, set.getTieBreakPoints(PlayerSide.FIRST));
            assertEquals(0, set.getTieBreakPoints(PlayerSide.SECOND));
        }

        @Test
        @DisplayName("getWinner: во время тайбрейка → empty")
        void getWinner_duringTieBreak_returnsEmpty() {
            score(PlayerSide.FIRST, 3);
            score(PlayerSide.SECOND, 2);

            assertTrue(set.getWinner().isEmpty());
        }

        @Test
        @DisplayName("getWinner: FIRST выигрывает тайбрейк 7-0 → сет 7-6, возвращает FIRST")
        void getWinner_whenFirstWinsTieBreakSevenToZero_returnsFirst() {
            score(PlayerSide.FIRST, 7);

            assertTrue(set.isFinished());
            assertEquals(7, set.getGames(PlayerSide.FIRST));
            assertEquals(6, set.getGames(PlayerSide.SECOND));
            assertEquals(PlayerSide.FIRST, set.getWinner().orElse(null));
        }

        @Test
        @DisplayName("getWinner: SECOND выигрывает extended-тайбрейк 8-6 → сет 7-6, возвращает SECOND")
        void getWinner_whenSecondWinsExtendedTieBreakEightToSix_returnsSecond() {
            score(PlayerSide.FIRST, 6);
            score(PlayerSide.SECOND, 6);

            score(PlayerSide.SECOND, 2);

            assertTrue(set.isFinished());
            assertEquals(7, set.getGames(PlayerSide.SECOND));
            assertEquals(6, set.getGames(PlayerSide.FIRST));
            assertEquals(PlayerSide.SECOND, set.getWinner().orElse(null));
        }

        @Test
        @DisplayName("getTieBreakPoints: после завершения сета → null")
        void getTieBreakPoints_afterSetFinished_returnsNull() {
            score(PlayerSide.FIRST, 7);

            assertNull(set.getTieBreakPoints(PlayerSide.FIRST));
            assertNull(set.getTieBreakPoints(PlayerSide.SECOND));
        }
    }

    @Nested
    @DisplayName("addPoint: исключения")
    class AddPointExceptions {

        @Test
        @DisplayName("addPoint: после завершения сета → IllegalStateException")
        void addPoint_afterSetFinished_throwsIllegalState() {
            winGamesFor(PlayerSide.FIRST, GAMES_TO_WIN);

            assertThrows(IllegalStateException.class, () -> set.addPoint(PlayerSide.FIRST));
        }

        @Test
        @DisplayName("addPoint: после завершения тайбрейка → IllegalStateException")
        void addPoint_afterTieBreakFinish_throwsIllegalState() {
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