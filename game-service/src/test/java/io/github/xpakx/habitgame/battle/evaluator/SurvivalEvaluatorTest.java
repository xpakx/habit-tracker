package io.github.xpakx.habitgame.battle.evaluator;

import io.github.xpakx.habitgame.battle.Battle;
import io.github.xpakx.habitgame.battle.BattleObjective;
import io.github.xpakx.habitgame.expedition.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SurvivalEvaluatorTest {
    private BattleResultEvaluator evaluator;

    public static Stream<Arguments> provideObjectives() {
        return Stream.of(
                Arguments.of(BattleObjective.DEFEAT),
                Arguments.of(BattleObjective.BOSS),
                Arguments.of(BattleObjective.SEIZE),
                Arguments.of(BattleObjective.ESCAPE)
        );
    }

    public static Stream<Arguments> provideListOfDestroyedShips() {
        return Stream.of(
                Arguments.of(getShipList(1, 0)),
                Arguments.of(getShipList(2, 0)),
                Arguments.of(getShipList(3, 0)),
                Arguments.of(getShipList(10, 0))
        );
    }

    private static List<Ship> getShipList(int destroyed, int healthy) {
        List<Ship> result = new ArrayList<>();
        for(int i = 0; i < destroyed; i++) {
            Ship ship = new Ship();
            ship.setEnemy(true);
            ship.setDestroyed(true);
            result.add(ship);
        }
        for(int i = 0; i < healthy; i++) {
            Ship ship = new Ship();
            ship.setEnemy(true);
            ship.setDestroyed(false);
            result.add(ship);
        }
        return result;
    }

    public static Stream<Arguments> provideListsWithHealthyShips() {
        return Stream.of(
                Arguments.of(getShipList(1, 1)),
                Arguments.of(getShipList(2, 1)),
                Arguments.of(getShipList(3, 5)),
                Arguments.of(getShipList(10, 1))
        );
    }

    @BeforeEach
    void setUp() {
        evaluator = new SurvivalEvaluator();
    }

    @Test
    void shouldAcceptType() {
        boolean result = evaluator.ofType(generateBattle(BattleObjective.SURVIVE));
        assertTrue(result);
    }

    private Battle generateBattle(BattleObjective type) {
        return generateBattle(type, 0, 0);
    }

    private Battle generateBattle(BattleObjective type, int turn, int turnsToWin) {
        Battle battle = new Battle();
        battle.setObjective(type);
        battle.setTurn(turn);
        battle.setTurnsToSurvive(turnsToWin);
        return battle;
    }

    @ParameterizedTest
    @MethodSource("provideObjectives")
    void shouldNotAcceptOtherBattleTypes(BattleObjective objective) {
        boolean result = evaluator.ofType(generateBattle(objective));
        assertFalse(result);
    }

    @ParameterizedTest
    @MethodSource("provideListOfDestroyedShips")
    void shouldEvaluateDestroyingEnemyShipsAsWinEvenBeforeFinalTurn(List<Ship> ships) {
        boolean result = evaluator.evaluate(generateBattle(BattleObjective.DEFEAT, 1, 10), ships);
        assertTrue(result);
    }

    @Test
    void shouldEvaluateEmptyShipListAsWinEvenBeforeFinalTurn() {
        boolean result = evaluator.evaluate(generateBattle(BattleObjective.DEFEAT, 1, 10),  new ArrayList<>());
        assertTrue(result);
    }

    @ParameterizedTest
    @MethodSource("provideListsWithHealthyShips")
    void shouldNotEvaluateAsWin(List<Ship> ships) {
        boolean result = evaluator.evaluate(generateBattle(BattleObjective.DEFEAT, 1, 10), ships);
        assertFalse(result);
    }

    @ParameterizedTest
    @MethodSource("provideListOfDestroyedShips")
    void shouldEvaluateListOfDestroyedShipsAsWinAtFinalTurn(List<Ship> ships) {
        boolean result = evaluator.evaluate(generateBattle(BattleObjective.DEFEAT, 10, 10), ships);
        assertTrue(result);
    }

    @Test
    void shouldEvaluateEmptyShipListAsWinAtFinalTurn() {
        boolean result = evaluator.evaluate(generateBattle(BattleObjective.DEFEAT, 10, 10),  new ArrayList<>());
        assertTrue(result);
    }

    @ParameterizedTest
    @MethodSource("provideListsWithHealthyShips")
    void shouldEvaluateAnyListAsWinAtFinalTurn(List<Ship> ships) {
        boolean result = evaluator.evaluate(generateBattle(BattleObjective.DEFEAT, 10, 10), ships);
        assertTrue(result);
    }
}