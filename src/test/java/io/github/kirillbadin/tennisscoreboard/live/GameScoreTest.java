package io.github.kirillbadin.tennisscoreboard.live;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameScore")
class GameScoreTest {
    private GameScore game;

    @BeforeEach
    void setUp() {
        game = new GameScore();
    }

    @Test
    @DisplayName("initial state: LOVE-LOVE, not finished")
    void initialState() {
        assertEquals(GamePoint.LOVE, game.getScoreFor(PlayerSide.FIRST));
        assertEquals(GamePoint.LOVE, game.getScoreFor(PlayerSide.SECOND));
        assertFalse(game.getWinner().isPresent());
    }

    @Nested
    @DisplayName("point progression")
    class PointProgression {

        @Test
        @DisplayName("LOVE → FIFTEEN")
        void loveToFifteen() {
            game.addPoint(PlayerSide.FIRST);
            assertEquals(GamePoint.FIFTEEN, game.getScoreFor(PlayerSide.FIRST));
            assertEquals(GamePoint.LOVE, game.getScoreFor(PlayerSide.SECOND));
        }

        @Test
        @DisplayName("FIFTEEN → THIRTY")
        void fifteenToThirty() {
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.FIRST);
            assertEquals(GamePoint.THIRTY, game.getScoreFor(PlayerSide.FIRST));
        }

        @Test
        @DisplayName("THIRTY → FORTY")
        void thirtyToForty() {
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.FIRST);
            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.FIRST));
        }

        @Test
        @DisplayName("both sides score independently")
        void bothSidesScore() {
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.SECOND);
            assertEquals(GamePoint.THIRTY, game.getScoreFor(PlayerSide.FIRST));
            assertEquals(GamePoint.FIFTEEN, game.getScoreFor(PlayerSide.SECOND));
        }
    }

    @Nested
    @DisplayName("winning")
    class Winning {

        @Test
        @DisplayName("FIRST wins 40-0")
        void firstWins40_0() {
            score(PlayerSide.FIRST, 4);
            assertEquals(PlayerSide.FIRST, game.getWinner().orElse(null));
        }

        @Test
        @DisplayName("FIRST wins from 40-30")
        void firstWins40_30() {
            score(PlayerSide.FIRST, 3);
            score(PlayerSide.SECOND, 2);
            game.addPoint(PlayerSide.FIRST);
            assertEquals(PlayerSide.FIRST, game.getWinner().orElse(null));
        }

        @Test
        @DisplayName("SECOND wins from 15-40")
        void secondWins15_40() {
            score(PlayerSide.FIRST, 1);
            score(PlayerSide.SECOND, 3);
            game.addPoint(PlayerSide.SECOND);
            assertEquals(PlayerSide.SECOND, game.getWinner().orElse(null));
        }
    }

    @Nested
    @DisplayName("deuce and advantage")
    class DeuceAndAdvantage {

        @BeforeEach
        void reachDeuce() {
            score(PlayerSide.FIRST, 3);
            score(PlayerSide.SECOND, 3);
        }

        @Test
        @DisplayName("40-40 is not finished")
        void deuceNotFinished() {
            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.FIRST));
            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.SECOND));
            assertFalse(game.getWinner().isPresent());
        }

        @Test
        @DisplayName("FIRST gets ADVANTAGE from deuce")
        void firstGetsAdvantage() {
            game.addPoint(PlayerSide.FIRST);
            assertEquals(GamePoint.ADVANTAGE, game.getScoreFor(PlayerSide.FIRST));
            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.SECOND));
            assertFalse(game.getWinner().isPresent());
        }

        @Test
        @DisplayName("SECOND gets ADVANTAGE from deuce")
        void secondGetsAdvantage() {
            game.addPoint(PlayerSide.SECOND);
            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.FIRST));
            assertEquals(GamePoint.ADVANTAGE, game.getScoreFor(PlayerSide.SECOND));
            assertFalse(game.getWinner().isPresent());
        }

        @Test
        @DisplayName("from ADVANTAGE opponent scores → back to deuce")
        void backToDeuce() {
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.SECOND);
            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.FIRST));
            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.SECOND));
            assertFalse(game.getWinner().isPresent());
        }

        @Test
        @DisplayName("FIRST wins from ADVANTAGE")
        void firstWinsFromAdvantage() {
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.FIRST);
            assertEquals(PlayerSide.FIRST, game.getWinner().orElse(null));
        }

        @Test
        @DisplayName("SECOND wins from ADVANTAGE")
        void secondWinsFromAdvantage() {
            game.addPoint(PlayerSide.SECOND);
            game.addPoint(PlayerSide.SECOND);
            assertEquals(PlayerSide.SECOND, game.getWinner().orElse(null));
        }

        @Test
        @DisplayName("multiple deuce cycles")
        void multipleDeuceCycles() {
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.SECOND);
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.SECOND);
            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.FIRST));
            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.SECOND));
            assertFalse(game.getWinner().isPresent());
        }
    }

    @Nested
    @DisplayName("exception handling")
    class ExceptionHandling {

        @Test
        @DisplayName("addPoint after game finished throws")
        void throwsAfterFinished() {
            score(PlayerSide.FIRST, 4);
            assertThrows(IllegalStateException.class, () -> game.addPoint(PlayerSide.FIRST));
        }

        @Test
        @DisplayName("addPoint after finished from advantage throws")
        void throwsAfterAdvantage() {
            score(PlayerSide.FIRST, 3);
            score(PlayerSide.SECOND, 3);
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.FIRST);
            assertThrows(IllegalStateException.class, () -> game.addPoint(PlayerSide.SECOND));
        }
    }

    private void score(PlayerSide side, int count) {
        for (int i = 0; i < count; i++) {
            game.addPoint(side);
        }
    }
}