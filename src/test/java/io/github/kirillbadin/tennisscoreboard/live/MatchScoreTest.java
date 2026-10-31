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
    @DisplayName("newMatch: 0-0 по сетам, матч не завершён")
    void newMatch_hasZeroSetsAndNoWinner() {
        assertEquals(0, match.getSets(PlayerSide.FIRST));
        assertEquals(0, match.getSets(PlayerSide.SECOND));
        assertFalse(match.isFinished());
        assertTrue(match.getWinner().isEmpty());
    }

    @Test
    @DisplayName("getSets: при незавершённом сете → 0")
    void getSets_withUnfinishedSet_returnsZero() {
        winGamesFor(PlayerSide.FIRST, GAMES_TO_WIN_SET - 1);
        assertEquals(0, match.getSets(PlayerSide.FIRST));
    }

    @Test
    @DisplayName("getGames: после завершения сета → счётчик нового сета (0)")
    void getGames_afterSetFinished_returnsZeroForNewSet() {
        winSetFor(PlayerSide.FIRST);   // 6-0, начался новый сет

        assertEquals(0, match.getGames(PlayerSide.FIRST));
        assertEquals(0, match.getGames(PlayerSide.SECOND));
        assertEquals(1, match.getSets(PlayerSide.FIRST));   // в сетах уже 1
    }

    @Nested
    @DisplayName("addPoint: прогрессия сетов")
    class AddPointSetProgression {

        @Test
        @DisplayName("addPoint: FIRST выигрывает первый сет → 1-0, матч не завершён")
        void addPoint_whenFirstWinsFirstSet_incrementsFirstSets() {
            winSetFor(PlayerSide.FIRST);

            assertEquals(1, match.getSets(PlayerSide.FIRST));
            assertEquals(0, match.getSets(PlayerSide.SECOND));
            assertFalse(match.isFinished());
        }

        @Test
        @DisplayName("addPoint: каждый берёт по сету → 1-1, матч не завершён")
        void addPoint_whenEachSideWinsOneSet_matchUnfinished() {
            winSetFor(PlayerSide.FIRST);
            winSetFor(PlayerSide.SECOND);

            assertEquals(1, match.getSets(PlayerSide.FIRST));
            assertEquals(1, match.getSets(PlayerSide.SECOND));
            assertFalse(match.isFinished());
        }
    }

    @Nested
    @DisplayName("getWinner: завершение матча")
    class GetWinnerFinishing {

        @Test
        @DisplayName("getWinner: FIRST выигрывает 2-0 → возвращает FIRST")
        void getWinner_whenFirstWinsTwoToZero_returnsFirst() {
            winSetFor(PlayerSide.FIRST);
            winSetFor(PlayerSide.FIRST);

            assertTrue(match.isFinished());
            assertEquals(SETS_TO_WIN_MATCH, match.getSets(PlayerSide.FIRST));
            assertEquals(PlayerSide.FIRST, match.getWinner().orElse(null));
        }

        @Test
        @DisplayName("getWinner: SECOND выигрывает 2-0 → возвращает SECOND")
        void getWinner_whenSecondWinsTwoToZero_returnsSecond() {
            winSetFor(PlayerSide.SECOND);
            winSetFor(PlayerSide.SECOND);

            assertTrue(match.isFinished());
            assertEquals(SETS_TO_WIN_MATCH, match.getSets(PlayerSide.SECOND));
            assertEquals(PlayerSide.SECOND, match.getWinner().orElse(null));
        }

        @Test
        @DisplayName("getWinner: FIRST выигрывает 2-1 → возвращает FIRST")
        void getWinner_whenFirstWinsTwoToOne_returnsFirst() {
            winSetFor(PlayerSide.SECOND);
            winSetFor(PlayerSide.FIRST);
            winSetFor(PlayerSide.FIRST);

            assertTrue(match.isFinished());
            assertEquals(2, match.getSets(PlayerSide.FIRST));
            assertEquals(1, match.getSets(PlayerSide.SECOND));
            assertEquals(PlayerSide.FIRST, match.getWinner().orElse(null));
        }

        @Test
        @DisplayName("getWinner: SECOND выигрывает 2-1 → возвращает SECOND")
        void getWinner_whenSecondWinsTwoToOne_returnsSecond() {
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
    @DisplayName("addPoint: исключения")
    class AddPointExceptions {

        @Test
        @DisplayName("addPoint: после завершения матча → IllegalStateException")
        void addPoint_afterMatchFinished_throwsIllegalState() {
            winSetFor(PlayerSide.FIRST);
            winSetFor(PlayerSide.FIRST);

            assertThrows(IllegalStateException.class,
                    () -> match.addPoint(PlayerSide.FIRST));
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