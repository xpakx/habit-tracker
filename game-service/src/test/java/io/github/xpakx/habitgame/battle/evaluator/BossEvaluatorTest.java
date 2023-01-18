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

class BossEvaluatorTest {
    private BattleResultEvaluator evaluator;

    public static Stream<Arguments> provideObjectives() {
        return Stream.of(
                Arguments.of(BattleObjective.DEFEAT),
                Arguments.of(BattleObjective.SEIZE),
                Arguments.of(BattleObjective.SURVIVE),
                Arguments.of(BattleObjective.ESCAPE)
        );
    }

    public static Stream<Arguments> provideWinningShipLists() {
        return Stream.of(
                Arguments.of(getShipList(5, true, true, true)),
                Arguments.of(getShipList(5, true, true, false)),
                Arguments.of(getShipList(5, false, true, false)),
                Arguments.of(getShipList(0, true, true, true))
        );
    }

    private static List<Ship> getShipList(int ships, boolean boss, boolean bossDestroyed, boolean shipsDestroyed) {
        List<Ship> result = new ArrayList<>();
        for(int i = 0; i < ships; i++) {
            Ship ship = new Ship();
            ship.setEnemy(true);
            ship.setDestroyed(shipsDestroyed);
            result.add(ship);
        }
        if(boss) {
            Ship ship = new Ship();
            ship.setEnemy(true);
            ship.setBoss(true);
            ship.setDestroyed(bossDestroyed);
            result.add(ship);
        }
        return result;
    }

    public static Stream<Arguments> provideNotWinningShipLists() {
        return Stream.of(
                Arguments.of(getShipList(5, true, false, true)),
                Arguments.of(getShipList(5, true, false, false)),
                Arguments.of(getShipList(0, true, false, true))
        );
    }

    @BeforeEach
    void setUp() {
        evaluator = new BossEvaluator();
    }

    @Test
    void shouldAcceptType() {
        boolean result = evaluator.ofType(generateBattleOfType(BattleObjective.BOSS));
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