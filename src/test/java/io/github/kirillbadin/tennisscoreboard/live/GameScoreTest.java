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
    @DisplayName("getScoreFor: при новой игре → LOVE обоим")
    void getScoreFor_withNewGame_returnsLoveForBoth() {
        assertEquals(GamePoint.LOVE, game.getScoreFor(PlayerSide.FIRST));
        assertEquals(GamePoint.LOVE, game.getScoreFor(PlayerSide.SECOND));
    }

    @Test
    @DisplayName("getWinner: при новой игре → empty")
    void getWinner_withNewGame_returnsEmpty() {
        assertTrue(game.getWinner().isEmpty());
    }

    @Nested
    @DisplayName("addPoint: прогрессия очков")
    class AddPointProgression {

        @Test
        @DisplayName("addPoint: с LOVE одно очко FIRST → возвращает FIFTEEN")
        void addPoint_withLoveScore_incrementsToFifteen() {
            game.addPoint(PlayerSide.FIRST);

            assertEquals(GamePoint.FIFTEEN, game.getScoreFor(PlayerSide.FIRST));
            assertEquals(GamePoint.LOVE, game.getScoreFor(PlayerSide.SECOND));
        }

        @Test
        @DisplayName("addPoint: с FIFTEEN одно очко FIRST → возвращает THIRTY")
        void addPoint_withFifteenScore_incrementsToThirty() {
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.FIRST);

            assertEquals(GamePoint.THIRTY, game.getScoreFor(PlayerSide.FIRST));
        }

        @Test
        @DisplayName("addPoint: с THIRTY одно очко FIRST → возвращает FORTY")
        void addPoint_withThirtyScore_incrementsToForty() {
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.FIRST);

            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.FIRST));
        }

        @Test
        @DisplayName("addPoint: очки каждой стороны считаются независимо")
        void addPoint_whenBothSidesScore_pointsAreIndependent() {
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.SECOND);

            assertEquals(GamePoint.THIRTY, game.getScoreFor(PlayerSide.FIRST));
            assertEquals(GamePoint.FIFTEEN, game.getScoreFor(PlayerSide.SECOND));
        }
    }

    @Nested
    @DisplayName("getWinner: победа в гейме")
    class GetWinnerWinning {

        @Test
        @DisplayName("getWinner: 40-0 и очко FIRST → возвращает FIRST")
        void getWinner_withFortyToZero_returnsFirst() {
            score(PlayerSide.FIRST, 3);

            game.addPoint(PlayerSide.FIRST);

            assertEquals(PlayerSide.FIRST, game.getWinner().orElse(null));
        }

        @Test
        @DisplayName("getWinner: 40-30 и очко FIRST → возвращает FIRST")
        void getWinner_withFortyThirty_returnsFirst() {
            score(PlayerSide.FIRST, 3);
            score(PlayerSide.SECOND, 2);

            game.addPoint(PlayerSide.FIRST);

            assertEquals(PlayerSide.FIRST, game.getWinner().orElse(null));
        }

        @Test
        @DisplayName("getWinner: 15-40 и очко SECOND → возвращает SECOND")
        void getWinner_withFifteenForty_returnsSecond() {
            score(PlayerSide.FIRST, 1);
            score(PlayerSide.SECOND, 3);

            game.addPoint(PlayerSide.SECOND);

            assertEquals(PlayerSide.SECOND, game.getWinner().orElse(null));
        }
    }

    @Nested
    @DisplayName("addPoint: deuce и advantage")
    class AddPointDeuceAndAdvantage {

        @BeforeEach
        void reachDeuce() {
            score(PlayerSide.FIRST, 3);
            score(PlayerSide.SECOND, 3);
        }

        @Test
        @DisplayName("getWinner: при 40-40 → empty")
        void getWinner_atDeuce_returnsEmpty() {
            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.FIRST));
            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.SECOND));

            assertTrue(game.getWinner().isEmpty());
        }

        @Test
        @DisplayName("addPoint: при deuce очко FIRST → FIRST advantage")
        void addPoint_atDeuce_givesAdvantageToFirst() {
            game.addPoint(PlayerSide.FIRST);

            assertEquals(GamePoint.ADVANTAGE, game.getScoreFor(PlayerSide.FIRST));
            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.SECOND));
            assertTrue(game.getWinner().isEmpty());
        }

        @Test
        @DisplayName("addPoint: при deuce очко SECOND → SECOND advantage")
        void addPoint_atDeuce_givesAdvantageToSecond() {
            game.addPoint(PlayerSide.SECOND);

            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.FIRST));
            assertEquals(GamePoint.ADVANTAGE, game.getScoreFor(PlayerSide.SECOND));
            assertTrue(game.getWinner().isEmpty());
        }

        @Test
        @DisplayName("addPoint: при advantage у FIRST, очко SECOND → снова deuce")
        void addPoint_withOpponentAdvantage_returnsToDeuce() {
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.SECOND);

            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.FIRST));
            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.SECOND));
            assertTrue(game.getWinner().isEmpty());
        }

        @Test
        @DisplayName("getWinner: при advantage у FIRST, очко FIRST → возвращает FIRST")
        void getWinner_withFirstAdvantage_returnsFirst() {
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.FIRST);

            assertEquals(PlayerSide.FIRST, game.getWinner().orElse(null));
        }

        @Test
        @DisplayName("getWinner: при advantage у SECOND, очко SECOND → возвращает SECOND")
        void getWinner_withSecondAdvantage_returnsSecond() {
            game.addPoint(PlayerSide.SECOND);
            game.addPoint(PlayerSide.SECOND);

            assertEquals(PlayerSide.SECOND, game.getWinner().orElse(null));
        }

        @Test
        @DisplayName("addPoint: после нескольких deuce-циклов гейм не завершён")
        void addPoint_withMultipleDeuceCycles_gameStillUnfinished() {
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.SECOND);
            game.addPoint(PlayerSide.FIRST);
            game.addPoint(PlayerSide.SECOND);

            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.FIRST));
            assertEquals(GamePoint.FORTY, game.getScoreFor(PlayerSide.SECOND));
            assertTrue(game.getWinner().isEmpty());
        }
    }

    @Nested
    @DisplayName("addPoint: исключения")
    class AddPointExceptions {

        @Test
        @DisplayName("addPoint: после победы FIRST добавление очков → IllegalStateException")
        void addPoint_afterGameFinished_throwsIllegalState() {
            score(PlayerSide.FIRST, 4);

            assertThrows(IllegalStateException.class, () -> game.addPoint(PlayerSide.FIRST));
        }

        @Test
        @DisplayName("addPoint: после завершения гейма от advantage добавление очков → IllegalStateException")
        void addPoint_afterAdvantageFinish_throwsIllegalState() {
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