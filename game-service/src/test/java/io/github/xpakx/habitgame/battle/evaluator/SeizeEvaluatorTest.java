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

class SeizeEvaluatorTest {
    private BattleResultEvaluator evaluator;

    public static Stream<Arguments> provideObjectives() {
        return Stream.of(
                Arguments.of(BattleObjective.DEFEAT),
                Arguments.of(BattleObjective.BOSS),
                Arguments.of(BattleObjective.SURVIVE),
                Arguments.of(BattleObjective.ESCAPE)
        );
    }

    public static Stream<Arguments> provideShipLists() {
        return Stream.of(
                Arguments.of(getShipList(0, 0)),
                Arguments.of(getShipList(1, 0)),
                Arguments.of(getShipList(2, 1)),
                Arguments.of(getShipList(3, 2)),
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

    @BeforeEach
    void setUp() {
        evaluator = new SeizeEvaluator();
    }

    @Test
    void shouldAcceptType() {
        boolean result = evaluator.ofType(generateBattle(BattleObjective.SEIZE));
        assertTrue(result);
    }

    private Battle generateBattle(BattleObjective type) {
        return generateBattle(type, true);
    }

    private Battle generateBattle(BattleObjective type, boolean seized) {
        Battle battle = new Battle();
        battle.setObjective(type);
        battle.setSeized(seized);
        return battle;
    }

    @ParameterizedTest
    @MethodSource("provideObjectives")
    void shouldNotAcceptOtherBattleTypes(BattleObjective objective) {
        boolean result = evaluator.ofType(generateBattle(objective));
        assertFalse(result);
    }

    @ParameterizedTest
    @MethodSource("provideShipLists")
    void shouldEvaluateAsWin(List<Ship> ships) {
        boolean result = evaluator.evaluate(generateBattle(BattleObjective.SEIZE, true), ships);
        assertTrue(result);
    }

    @ParameterizedTest
    @MethodSource("provideShipLists")
    void shouldNotEvaluateAsWin(List<Ship> ships) {
        boolean result = evaluator.evaluate(generateBattle(BattleObjective.SEIZE, false), ships);
        assertFalse(result);
    }

}