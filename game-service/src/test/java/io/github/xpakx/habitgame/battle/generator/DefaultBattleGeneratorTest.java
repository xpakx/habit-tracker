package io.github.xpakx.habitgame.battle.generator;

import io.github.xpakx.habitgame.battle.Battle;
import io.github.xpakx.habitgame.battle.BattleObjective;
import io.github.xpakx.habitgame.battle.BattleRepository;
import io.github.xpakx.habitgame.battle.Position;
import io.github.xpakx.habitgame.expedition.Expedition;
import io.github.xpakx.habitgame.expedition.ExpeditionResult;
import io.github.xpakx.habitgame.expedition.Ship;
import io.github.xpakx.habitgame.expedition.ShipRepository;
import io.github.xpakx.habitgame.ship.ShipType;
import io.github.xpakx.habitgame.ship.ShipTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

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

    private Battle getBattle() {
        return new Battle();
    }

    private void initMocks() {
        generator = new DefaultBattleGenerator(battleRepository, shipRepository, shipTypeRepository);
    }

    @Test
    void shouldUseCorrectObjectiveForGeneratedBattle() {
        initMocks();
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
        initMocks();
        Battle result = generator.createBattle(getExpeditionResult());
        assertThat(result, is(notNullValue()));
        assertThat(result, hasProperty("finished", equalTo(false)));
        assertThat(result, hasProperty("started", equalTo(false)));
        assertThat(result, hasProperty("turn", equalTo(0)));
    }

    @Test
    void generatedBattleShouldHaveNonZeroHeightAndWidth() {
        initMocks();
        Battle result = generator.createBattle(getExpeditionResult());
        assertThat(result, is(notNullValue()));
        assertThat(result, hasProperty("height", is(notNullValue())));
        assertThat(result, hasProperty("height", is(greaterThan(0))));
        assertThat(result, hasProperty("width", is(notNullValue())));
        assertThat(result, hasProperty("width", is(greaterThan(0))));
    }

    @Test
    void shouldGenerateEmptyShipListForEmptyExpedition() {
        Mockito.when(shipRepository.findByExpeditionId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        initMocks();
        List<Ship> result = generator.generateShips(1L, getExpedition(), rnd);
        assertThat(result, hasSize(0));
    }

    private Expedition getExpedition() {
        Expedition expedition = new Expedition();
        expedition.setId(1L);
        expedition.setUserId(1L);
        return expedition;
    }

    private void initShipPrototypes(Integer... rarities) {
        List<ShipType> types = new ArrayList<>();
        for(int rarity : rarities) {
            types.add(createShipPrototype(rarity, "ship"+rarity, (long) rarity));
        }
        Mockito.when(shipTypeRepository.findRandomTypes(Mockito.anyInt(), Mockito.anyInt())).thenReturn(types);
    }

    private ShipType createShipPrototype(int rarity, String name, Long id) {
        ShipType type = new ShipType();
        type.setRarity(rarity);
        type.setBaseSize(1);
        type.setCode(name.toUpperCase());
        type.setCriticalRate(5);
        type.setHitRate(90);
        type.setHp(20);
        type.setStrength(3);
        type.setId(id);
        type.setName(name);
        type.setImgUrl("");
        return type;
    }

    @Test
    void shouldGenerateShipList() {
        Mockito.when(shipRepository.findByExpeditionId(Mockito.anyLong())).thenReturn(generateShips(1));
        initShipPrototypes(1);
        initMocks();
        List<Ship> result = generator.generateShips(1L, getExpedition(), rnd);
        assertThat(result, hasSize(greaterThan(0)));
    }

    private List<Ship> generateShips(int... rarities) {
        List<Ship> ships = new ArrayList<>();
        int id = 0;
        for(int rarity : rarities) {
            ships.add(createShip(rarity, "ship"+id++));
        }
        return ships;
    }

    private Ship createShip(int rarity, String name) {
        Ship ship = new Ship();
        ship.setName(name);
        ship.setRarity(rarity);
        return ship;
    }

    @Test
    void shouldGenerateEmptyPositionListForEmptyShipList() {
        Mockito.when(battleRepository.getReferenceById(Mockito.anyLong())).thenReturn(getBattle());
        initMocks();
        List<Position> result = generator.randomizePositions(new ArrayList<>(), 1L, rnd);
        assertThat(result, hasSize(0));
    }

    @Test
    void shouldGeneratePositionListForEveryShip() {
        Mockito.when(battleRepository.getReferenceById(Mockito.anyLong())).thenReturn(getBattle());
        initMocks();
        List<Ship> ships = generateShips(1,1,1,2,3);
        List<Position> result = generator.randomizePositions(ships, 1L, rnd);
        assertThat(result, hasSize(equalTo(ships.size())));
        assertThat(result, hasItem(hasProperty("ship", sameInstance(ships.get(0)))));
        assertThat(result, hasItem(hasProperty("ship", sameInstance(ships.get(1)))));
        assertThat(result, hasItem(hasProperty("ship", sameInstance(ships.get(2)))));
        assertThat(result, hasItem(hasProperty("ship", sameInstance(ships.get(3)))));
        assertThat(result, hasItem(hasProperty("ship", sameInstance(ships.get(4)))));
    }

    @Test
    void thereShouldBeNoBossInGeneratedShips() {
        Mockito.when(shipRepository.findByExpeditionId(Mockito.anyLong())).thenReturn(generateShips(1,2));
        initShipPrototypes(1,2);
        initMocks();
        List<Ship> result = generator.generateShips(1L, getExpedition(), rnd);
        assertThat(result, hasSize(greaterThan(0)));
        assertThat(result, everyItem(not(hasProperty("boss", equalTo(true)))));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 5, 10, 100, 15000})
    void shouldGenerateMultipleShipsWithSameRarity(int ships) {
        int[] list = Collections.nCopies(ships, 1).stream().flatMapToInt(IntStream::of).toArray();
        Mockito.when(shipRepository.findByExpeditionId(Mockito.anyLong())).thenReturn(generateShips(list));
        initShipPrototypes(1,2);
        initMocks();
        List<Ship> result = generator.generateShips(1L, getExpedition(), rnd);
        assertThat(result, hasSize(greaterThan(0)));
        assertThat(result, everyItem(not(hasProperty("boss", equalTo(true)))));
    }
}