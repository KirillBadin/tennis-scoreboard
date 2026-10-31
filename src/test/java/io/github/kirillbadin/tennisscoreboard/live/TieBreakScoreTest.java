package io.github.kirillbadin.tennisscoreboard.live;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TieBreakScore")
class TieBreakScoreTest {
    private TieBreakScore tieBreak;

    @BeforeEach
    void setUp(){
        tieBreak = new TieBreakScore();
    }

    @Test
    @DisplayName("newTieBreak: 0-0, победителя нет")
    void newTieBreak_hasZeroPointsAndNoWinner() {
        assertEquals(0, tieBreak.getPoints(PlayerSide.FIRST));
        assertEquals(0, tieBreak.getPoints(PlayerSide.SECOND));
        assertTrue(tieBreak.getWinner().isEmpty());
    }

    @Test
    @DisplayName("addPoint: обе стороны набирают очки независимо")
    void addPoint_whenBothSidesScore_pointsAreIndependent() {
        tieBreak.addPoint(PlayerSide.FIRST);
        assertEquals(1, tieBreak.getPoints(PlayerSide.FIRST));
        assertEquals(0, tieBreak.getPoints(PlayerSide.SECOND));

        tieBreak.addPoint(PlayerSide.SECOND);
        assertEquals(1, tieBreak.getPoints(PlayerSide.FIRST));
        assertEquals(1, tieBreak.getPoints(PlayerSide.SECOND));
    }

    @Nested
    @DisplayName("getWinner: победа (мин 7 , отрыв 2)")
    class GetWinnerWinning {

        @Test
        @DisplayName("getWinner: FIRST выигрывает 7-0 → возвращает FIRST")
        void getWinner_whenFirstWinsSevenToZero_returnsFirst() {
            score(PlayerSide.FIRST, 7);

            assertEquals(PlayerSide.FIRST, tieBreak.getWinner().orElse(null));
        }

        @Test
        @DisplayName("getWinner: SECOND выигрывает 7-5 → возвращает SECOND")
        void getWinner_whenSecondWinsSevenToFive_returnsSecond() {
            score(PlayerSide.FIRST, 5);
            score(PlayerSide.SECOND, 7);

            assertEquals(PlayerSide.SECOND, tieBreak.getWinner().orElse(null));
        }

        @Test
        @DisplayName("getWinner: при 7-6 → empty (нужен отрыв в 2 очка)")
        void getWinner_atSevenSix_returnsEmpty() {
            score(PlayerSide.FIRST, 6);
            score(PlayerSide.SECOND, 6);
            tieBreak.addPoint(PlayerSide.FIRST);

            assertTrue(tieBreak.getWinner().isEmpty());
        }

        @Test
        @DisplayName("getWinner: FIRST выигрывает extended-тайбрейк 8-6 → возвращает FIRST")
        void getWinner_whenFirstWinsExtendedEightToSix_returnsFirst() {
            score(PlayerSide.FIRST, 6);
            score(PlayerSide.SECOND, 6);
            tieBreak.addPoint(PlayerSide.FIRST);
            tieBreak.addPoint(PlayerSide.FIRST);

            assertEquals(PlayerSide.FIRST, tieBreak.getWinner().orElse(null));
        }

        @Test
        @DisplayName("getWinner: SECOND выигрывает extended-тайбрейк 9-7 → возвращает SECOND")
        void getWinner_whenSecondWinsExtendedNineToSeven_returnsSecond() {
            score(PlayerSide.FIRST, 6);
            score(PlayerSide.SECOND, 6);

            tieBreak.addPoint(PlayerSide.FIRST);
            tieBreak.addPoint(PlayerSide.SECOND);
            tieBreak.addPoint(PlayerSide.SECOND);
            tieBreak.addPoint(PlayerSide.SECOND);

            assertEquals(PlayerSide.SECOND, tieBreak.getWinner().orElse(null));
        }

        @Test
        @DisplayName("getWinner: при 8-7 → empty (нужен отрыв в 2 очка)")
        void getWinner_atEightSeven_returnsEmpty() {
            score(PlayerSide.FIRST, 6);
            score(PlayerSide.SECOND, 6);

            tieBreak.addPoint(PlayerSide.FIRST);
            tieBreak.addPoint(PlayerSide.SECOND);
            tieBreak.addPoint(PlayerSide.FIRST);

            assertTrue(tieBreak.getWinner().isEmpty());
        }
    }

    @Nested
    @DisplayName("addPoint: исключения")
    class AddPointExceptions {

        @Test
        @DisplayName("addPoint: после завершения тайбрейка → IllegalStateException")
        void addPoint_afterTieBreakFinished_throwsIllegalState() {
            score(PlayerSide.FIRST, 7);

            assertThrows(IllegalStateException.class, () -> tieBreak.addPoint(PlayerSide.FIRST));
        }

        @Test
        @DisplayName("addPoint: после завершения extended-тайбрейка → IllegalStateException")
        void addPoint_afterExtendedFinish_throwsIllegalState() {
            score(PlayerSide.FIRST, 6);
            score(PlayerSide.SECOND, 6);
            score(PlayerSide.FIRST, 2);

            assertThrows(IllegalStateException.class, () -> tieBreak.addPoint(PlayerSide.SECOND));
        }
    }

    private void score(PlayerSide playerSide, int count) {
        for (int j = 0; j < count; j++) {
            tieBreak.addPoint(playerSide);
        }
    }
}