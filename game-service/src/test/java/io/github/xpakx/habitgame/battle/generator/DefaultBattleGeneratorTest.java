package io.github.xpakx.habitgame.battle.generator;

import io.github.xpakx.habitgame.battle.Battle;
import io.github.xpakx.habitgame.battle.BattleObjective;
import io.github.xpakx.habitgame.battle.BattleRepository;
import io.github.xpakx.habitgame.expedition.Expedition;
import io.github.xpakx.habitgame.expedition.ExpeditionResult;
import io.github.xpakx.habitgame.expedition.ShipRepository;
import io.github.xpakx.habitgame.ship.ShipTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class DefaultBattleGeneratorTest {
    private BattleGenerator generator;
    private final static Random rnd = new Random(210832879274L);
    @Mock
    private BattleRepository battleRepository;
    @Mock
    private ShipRepository shipRepository;
    @Mock
    private ShipTypeRepository shipTypeRepository;

    @BeforeEach
    void setUp() {
        generator = new DefaultBattleGenerator(battleRepository, shipRepository, shipTypeRepository);
    }

    @Test
    void shouldUseCorrectObjectiveForGeneratedBattle() {
        Battle result = generator.createBattle(getExpeditionResult());
        assertThat(result, is(notNullValue()));
        assertThat(result, hasProperty("objective", equalTo(BattleObjective.DEFEAT)));
    }

    private ExpeditionResult getExpeditionResult() {
        Expedition exp = new Expedition();
        ExpeditionResult result = new ExpeditionResult();
        result.setExpedition(exp);
        return result;
    }

    @Test
    void generatedBattleShouldHaveCorrectInitialValues() {
        Battle result = generator.createBattle(getExpeditionResult());
        assertThat(result, is(notNullValue()));
        assertThat(result, hasProperty("finished", equalTo(false)));
        assertThat(result, hasProperty("started", equalTo(false)));
        assertThat(result, hasProperty("turn", equalTo(0)));
    }

    @Test
    void generatedBattleShouldHaveNonZeroHeightAndWidth() {
        Battle result = generator.createBattle(getExpeditionResult());
        assertThat(result, is(notNullValue()));
        assertThat(result, hasProperty("height", is(notNullValue())));
        assertThat(result, hasProperty("height", is(greaterThan(0))));
        assertThat(result, hasProperty("width", is(notNullValue())));
        assertThat(result, hasProperty("width", is(greaterThan(0))));
    }
}