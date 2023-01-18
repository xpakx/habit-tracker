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
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DefeatEvaluatorTest {
    private BattleResultEvaluator evaluator;
    private final static Random rnd = new Random(210832879274L);

    public static Stream<Arguments> provideObjectives() {
        return Stream.of(
                Arguments.of(BattleObjective.BOSS),
                Arguments.of(BattleObjective.SEIZE),
                Arguments.of(BattleObjective.SURVIVE),
                Arguments.of(BattleObjective.ESCAPE)
        );
    }

    public static Stream<Arguments> provideWinningShipLists() {
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

    public static Stream<Arguments> provideNotWinningShipLists() {
        return Stream.of(
                Arguments.of(getShipList(1, 1)),
                Arguments.of(getShipList(2, 1)),
                Arguments.of(getShipList(3, 5)),
                Arguments.of(getShipList(10, 1))
        );
    }

    @BeforeEach
    void setUp() {
        evaluator = new DefeatEvaluator();
    }

    @Test
    void shouldAcceptType() {
        boolean result = evaluator.ofType(generateBattleOfType(BattleObjective.DEFEAT));
        assertTrue(result);
    }

    private Battle generateBattleOfType(BattleObjective type) {
        Battle battle = new Battle();
        battle.setObjective(type);
        return battle;
    }

    @ParameterizedTest
    @MethodSource("provideObjectives")
    void shouldNotAcceptOtherBattleTypes(BattleObjective objective) {
        boolean result = evaluator.ofType(generateBattleOfType(objective));
        assertFalse(result);
    }

    @ParameterizedTest
    @MethodSource("provideWinningShipLists")
    void shouldEvaluateAsWin(List<Ship> ships) {
        boolean result = evaluator.evaluate(generateBattleOfType(BattleObjective.DEFEAT), ships);
        assertTrue(result);
    }

    @Test
    void shouldEvaluateEmptyShipListAsWin() {
        boolean result = evaluator.evaluate(generateBattleOfType(BattleObjective.DEFEAT),  new ArrayList<>());
        assertTrue(result);
    }

    @ParameterizedTest
    @MethodSource("provideNotWinningShipLists")
    void shouldNotEvaluateAsWin(List<Ship> ships) {
        boolean result = evaluator.evaluate(generateBattleOfType(BattleObjective.DEFEAT), ships);
        assertFalse(result);
    }
}