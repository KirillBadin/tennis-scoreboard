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
    @DisplayName("initial state: 0-0, not finished")
    void initialState() {
        assertEquals(0, tieBreak.getPoints(PlayerSide.FIRST));
        assertEquals(0, tieBreak.getPoints(PlayerSide.SECOND));
        assertFalse(tieBreak.getWinner().isPresent());
    }

    @Test
    @DisplayName("points increment independently")
    void pointsIncrement() {
        tieBreak.addPoint(PlayerSide.FIRST);
        assertEquals(1, tieBreak.getPoints(PlayerSide.FIRST));
        assertEquals(0, tieBreak.getPoints(PlayerSide.SECOND));

        tieBreak.addPoint(PlayerSide.SECOND);
        assertEquals(1, tieBreak.getPoints(PlayerSide.FIRST));
        assertEquals(1, tieBreak.getPoints(PlayerSide.SECOND));
    }

    @Nested
    @DisplayName("winning (min 7, diff 2)")
    class Winning {

        @Test
        @DisplayName("FIRST wins 7-0")
        void firstWins7_0() {
            score(PlayerSide.FIRST, 7);
            assertEquals(PlayerSide.FIRST, tieBreak.getWinner().orElse(null));
        }

        @Test
        @DisplayName("SECOND wins 7-5")
        void secondWins7_5() {
            score(PlayerSide.FIRST, 5);
            score(PlayerSide.SECOND, 7);
            assertEquals(PlayerSide.SECOND, tieBreak.getWinner().orElse(null));
        }

        @Test
        @DisplayName("7-6 not enough")
        void notFinishedAt7_6() {
            score(PlayerSide.FIRST, 6);
            score(PlayerSide.SECOND, 6);
            tieBreak.addPoint(PlayerSide.FIRST);
            assertFalse(tieBreak.getWinner().isPresent());
        }

        @Test
        @DisplayName("FIRST wins 8-6")
        void firstWins8_6() {
            score(PlayerSide.FIRST, 6);
            score(PlayerSide.SECOND, 6);
            tieBreak.addPoint(PlayerSide.FIRST);
            tieBreak.addPoint(PlayerSide.FIRST);
            assertEquals(PlayerSide.FIRST, tieBreak.getWinner().orElse(null));
        }

        @Test
        @DisplayName("SECOND wins 9-7")
        void secondWins9_7() {
            score(PlayerSide.FIRST, 6);
            score(PlayerSide.SECOND, 6);
            tieBreak.addPoint(PlayerSide.FIRST);
            tieBreak.addPoint(PlayerSide.SECOND);
            tieBreak.addPoint(PlayerSide.SECOND);
            tieBreak.addPoint(PlayerSide.SECOND);
            assertEquals(PlayerSide.SECOND, tieBreak.getWinner().orElse(null));
        }

        @Test
        @DisplayName("8-7 not enough")
        void notFinishedAt8_7() {
            score(PlayerSide.FIRST, 6);
            score(PlayerSide.SECOND, 6);
            tieBreak.addPoint(PlayerSide.FIRST);
            tieBreak.addPoint(PlayerSide.SECOND);
            tieBreak.addPoint(PlayerSide.FIRST);
            assertFalse(tieBreak.getWinner().isPresent());
        }
    }

    @Nested
    @DisplayName("exception handling")
    class ExceptionHandling {

        @Test
        @DisplayName("addPoint after finish throws")
        void throwsAfterFinish() {
            score(PlayerSide.FIRST, 7);
            assertThrows(IllegalStateException.class, () -> tieBreak.addPoint(PlayerSide.FIRST));
        }

        @Test
        @DisplayName("addPoint after extended finish throws")
        void throwsAfterExtended() {
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