package io.github.xpakx.habitgame.battle;

import io.github.xpakx.habitgame.battle.dto.MoveAction;
import io.github.xpakx.habitgame.battle.dto.MoveRequest;
import io.github.xpakx.habitgame.expedition.*;
import io.github.xpakx.habitgame.ship.ShipType;
import io.github.xpakx.habitgame.ship.ShipTypeRepository;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BattleControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;


    @Autowired
    ExpeditionResultRepository resultRepository;
    @Autowired
    ExpeditionRepository expeditionRepository;
    @Autowired
    BattleRepository battleRepository;
    @Autowired
    ShipRepository shipRepository;
    @Autowired
    PositionRepository positionRepository;
    @Autowired
    ShipTypeRepository typeRepository;
    @Autowired
    TerrainTypeRepository terrainRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        typeRepository.deleteAll();
        positionRepository.deleteAll();
        battleRepository.deleteAll();
        resultRepository.deleteAll();
        shipRepository.deleteAll();
        expeditionRepository.deleteAll();
        terrainRepository.deleteAll();
    }

    private Header getHeaderForUserId(Long userId) {
        return new Header("id", userId.toString());
    }

    @Test
    void shouldRespondWith401ToStartBattleIfNoUserIdIsGiven() {
        when()
                .get(baseUrl + "/expedition/{expeditionId}/battle", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToStartBattleIfExpeditionDoesNotExist() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/battle", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private Long addExpedition() {
        Expedition expedition = new Expedition();
        expedition.setUserId(userId);
        expedition.setFinished(false);
        return expeditionRepository.save(expedition).getId();
    }

    private void addResult(Long expeditionId, ResultType type, boolean completed) {
        ExpeditionResult result = new ExpeditionResult();
        result.setExpedition(expeditionRepository.getReferenceById(expeditionId));
        result.setCompleted(completed);
        result.setType(type);
        resultRepository.save(result);
    }

    @Test
    void shouldNotStartBattleForCompletedResult() {
        Long expeditionId = addExpedition();
        addResult(expeditionId, ResultType.BATTLE, true);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/battle", expeditionId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotStartBattleForNonBattleResultType() {
        Long expeditionId = addExpedition();
        addResult(expeditionId, ResultType.ISLAND, false);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/battle", expeditionId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldStartBattle() {
        Long expeditionId = addExpedition();
        addResult(expeditionId, ResultType.BATTLE, false);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/battle", expeditionId)
        .then()
                .statusCode(OK.value());
    }

    @Test
    void shouldAddNewBattleToDb() {
        Long expeditionId = addExpedition();
        addResult(expeditionId, ResultType.BATTLE, false);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/battle", expeditionId);
        List<Battle> battles = battleRepository.findAll();
        assertThat(battles, hasSize(1));
    }

    @Test
    void shouldRespondWith401ToPreparePositionIfNoUserIdGiven() {
        when()
                .post(baseUrl + "/battle/{battleId}/position", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToPreparePositionIfBattleDoesNotExist() {
        MoveRequest request = getMoveRequest(1,1, MoveAction.PREPARE, 1L);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
                .log().body()
        .when()
                .post(baseUrl + "/battle/{battleId}/position", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private MoveRequest getMoveRequest(Integer x, Integer y, MoveAction action, long ship) {
        MoveRequest request = new MoveRequest();
        request.setX(x);
        request.setY(y);
        request.setShipId(ship);
        request.setAction(action);
        return request;
    }

    @Test
    void shouldNotPlaceShipIfShipDoesNotExist() {
        MoveRequest request = getMoveRequest(1,1, MoveAction.PREPARE, 1L);
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/position", battleId)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private Long addBattle(Long expeditionId) {
        return addBattle(expeditionId, false, false);
    }

    private Long addBattle(Long expeditionId, boolean started) {
        return addBattle(expeditionId, started, false);
    }

    private Long addBattle(Long expeditionId, boolean started, boolean finished) {
        return addBattle(expeditionId, started, finished, BattleObjective.DEFEAT);
    }

    private Long addBattle(Long expeditionId, boolean started, boolean finished, BattleObjective objective) {
        Battle battle = new Battle();
        battle.setStarted(started);
        battle.setFinished(finished);
        battle.setExpedition(expeditionRepository.getReferenceById(expeditionId));
        battle.setTurn(0);
        battle.setWidth(10);
        battle.setHeight(10);
        battle.setObjective(objective);
        return battleRepository.save(battle).getId();
    }

    private Long addSeizedBattle(Long expeditionId) {
        Battle battle = new Battle();
        battle.setStarted(true);
        battle.setFinished(false);
        battle.setSeized(true);
        battle.setExpedition(expeditionRepository.getReferenceById(expeditionId));
        battle.setTurn(0);
        battle.setWidth(10);
        battle.setHeight(10);
        battle.setObjective(BattleObjective.SEIZE);
        return battleRepository.save(battle).getId();
    }

    private Long addSurvivalBattle(Long expeditionId, int turns, int turn) {
        Battle battle = new Battle();
        battle.setStarted(true);
        battle.setFinished(false);
        battle.setExpedition(expeditionRepository.getReferenceById(expeditionId));
        battle.setTurn(turn);
        battle.setTurnsToSurvive(turns);
        battle.setWidth(10);
        battle.setHeight(10);
        battle.setObjective(BattleObjective.SURVIVE);
        return battleRepository.save(battle).getId();
    }

    @Test
    void shouldNotPlaceShipIfWrongActionType() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId);
        Long shipId = addShip(expeditionId);
        MoveRequest request = getMoveRequest(1,1, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/position", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private Long addShip(Long expeditionId) {
        return addShip(expeditionId, false, false, 3);
    }

    private Long addPreparedShip(Long expeditionId) {
        Ship ship = new Ship();
        ship.setExpedition(expeditionRepository.getReferenceById(expeditionId));
        ship.setUserId(userId);
        ship.setSize(0);
        ship.setPrepared(true);
        return shipRepository.save(ship).getId();
    }


    private Long addEnemyShip(Long expeditionId) {
        return addEnemyShip(expeditionId, false, false);
    }

    private Long addEnemyShip(Long expeditionId, boolean movement, boolean action) {
        Ship ship = new Ship();
        ship.setExpedition(expeditionRepository.getReferenceById(expeditionId));
        ship.setUserId(userId);
        ship.setSize(1);
        ship.setEnemy(true);
        ship.setMovement(movement);
        ship.setAction(action);
        ship.setHp(1);
        ship.setRarity(2);
        ship.setStrength(1);
        ship.setHitRate(100);
        ship.setCriticalRate(0);
        ship.setAttackRange(3);
        ship.setMovementRange(3);
        return shipRepository.save(ship).getId();
    }

    private Long addDestroyedEnemyShip(Long expeditionId) {
        Ship ship = new Ship();
        ship.setExpedition(expeditionRepository.getReferenceById(expeditionId));
        ship.setUserId(userId);
        ship.setSize(1);
        ship.setEnemy(true);
        ship.setMovement(true);
        ship.setAction(true);
        ship.setDestroyed(true);
        ship.setHp(1);
        ship.setRarity(2);
        ship.setStrength(1);
        ship.setHitRate(100);
        ship.setCriticalRate(0);
        ship.setAttackRange(3);
        ship.setMovementRange(3);
        return shipRepository.save(ship).getId();
    }

    private Long addShip(Long expeditionId, int size) {
        return addShip(expeditionId, false, false, size);
    }

    private Long addShip(Long expeditionId, boolean movement, boolean action) {
        return addShip(expeditionId, movement, action, 3);
    }

    private Long addShip(Long expeditionId, boolean movement, boolean action, int size) {
        Ship ship = new Ship();
        ship.setExpedition(expeditionRepository.getReferenceById(expeditionId));
        ship.setUserId(userId);
        ship.setMovement(movement);
        ship.setAction(action);
        ship.setSize(size);
        ship.setHp(size);
        ship.setRarity(2);
        ship.setStrength(1);
        ship.setHitRate(100);
        ship.setCriticalRate(0);
        ship.setAttackRange(3);
        ship.setMovementRange(3);
        return shipRepository.save(ship).getId();
    }

    private Long addDestroyedShip(Long expeditionId) {
        Ship ship = new Ship();
        ship.setExpedition(expeditionRepository.getReferenceById(expeditionId));
        ship.setUserId(userId);
        ship.setMovement(false);
        ship.setAction(false);
        ship.setSize(0);
        ship.setHp(0);
        ship.setDestroyed(true);
        ship.setDamaged(true);
        ship.setAttackRange(3);
        ship.setMovementRange(3);
        return shipRepository.save(ship).getId();
    }

    @Test
    void shouldNotPlaceShipIfBattleAlreadyStarted() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        MoveRequest request = getMoveRequest(1,1, MoveAction.PREPARE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/position", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotPlaceShipIfCoordinateIsNull() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId);
        Long shipId = addShip(expeditionId);
        MoveRequest request = getMoveRequest(null,1, MoveAction.PREPARE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/position", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotPlaceShipIfFieldIsOccupied() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId);
        Long placedShipId = addShip(expeditionId);
        placeShip(placedShipId, 1, 1, battleId);
        Long shipId = addShip(expeditionId);
        MoveRequest request = getMoveRequest(1,1, MoveAction.PREPARE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/position", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private void placeShip(Long shipId, int x, int y, Long battleId) {
        Position position = new Position();
        position.setY(y);
        position.setX(x);
        position.setShip(shipRepository.getReferenceById(shipId));
        position.setBattle(battleRepository.getReferenceById(battleId));
        positionRepository.save(position);
    }

    @Test
    void shouldPlaceShip() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId);
        Long shipId = addShip(expeditionId);
        MoveRequest request = getMoveRequest(1,1, MoveAction.PREPARE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/position", battleId)
        .then()
                .statusCode(OK.value())
                .body("success", equalTo(true));
    }

    @Test
    void shouldAddPositionToDb() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId);
        Long shipId = addShip(expeditionId);
        MoveRequest request = getMoveRequest(1,1, MoveAction.PREPARE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/position", battleId);
        List<Position> positions = positionRepository.findAll();
        assertThat(positions, hasSize(1));
        assertThat(positions.get(0), hasProperty("x", equalTo(1)));
        assertThat(positions.get(0), hasProperty("y", equalTo(1)));
    }

    @Test
    void shouldUpdateShipInDb() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId);
        Long shipId = addShip(expeditionId);
        MoveRequest request = getMoveRequest(1,1, MoveAction.PREPARE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/position", battleId);
        Optional<Ship> ship = shipRepository.findById(shipId);
        assertTrue(ship.isPresent());
        assertThat(ship.get(), hasProperty("prepared", equalTo(true)));
    }


    @Test
    void shouldPlaceSecondShip() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId);
        Long shipId = addShip(expeditionId);
        Long placedShipId = addShip(expeditionId);
        placeShip(placedShipId, 2, 1, battleId);
        MoveRequest request = getMoveRequest(1,1, MoveAction.PREPARE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/position", battleId)
        .then()
                .statusCode(OK.value())
                .body("success", equalTo(true));
    }

    @Test
    void shouldReplaceShipPosition() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 2, 1, battleId);
        MoveRequest request = getMoveRequest(1,1, MoveAction.PREPARE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/position", battleId)
        .then()
                .statusCode(OK.value())
                .body("success", equalTo(true));
        List<Position> positions = positionRepository.findAll();
        assertThat(positions, hasSize(1));
        assertThat(positions.get(0), hasProperty("x", equalTo(1)));
        assertThat(positions.get(0), hasProperty("y", equalTo(1)));
    }

    @Test
    void shouldRespondWith401ToMoveIfNoUserIdGiven() {
        when()
                .post(baseUrl + "/battle/{battleId}/move", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToMoveIfBattleDoesNotExist() {
        MoveRequest request = getMoveRequest(1,1, MoveAction.MOVE, 1L);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldNotMoveIfShipDoesNotExist() {
        MoveRequest request = getMoveRequest(1,1, MoveAction.MOVE, 1L);
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldNotMoveIfWrongActionType() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        MoveRequest request = getMoveRequest(1,1, MoveAction.PREPARE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotMoveIfBattleIsNotStarted() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, false);
        Long shipId = addShip(expeditionId);
        MoveRequest request = getMoveRequest(1,1, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotMoveIfBattleIsFinished() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, true);
        Long shipId = addShip(expeditionId);
        MoveRequest request = getMoveRequest(1,1, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotMoveIfCoordinateIsNull() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        MoveRequest request = getMoveRequest(null,1, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotMoveIfFieldIsOccupied() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long placedShipId = addShip(expeditionId);
        placeShip(placedShipId, 1, 1, battleId);
        Long shipId = addShip(expeditionId);
        MoveRequest request = getMoveRequest(1,1, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldMoveShip() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 1, 1, battleId);
        MoveRequest request = getMoveRequest(2,2, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(OK.value())
                .body("success", equalTo(true));
    }

    @Test
    void shouldChangePositionInDbAfterMovement() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 1, 1, battleId);
        MoveRequest request = getMoveRequest(2,2, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId);
        List<Position> positions = positionRepository.findAll();
        assertThat(positions, hasSize(1));
        assertThat(positions.get(0), hasProperty("x", equalTo(2)));
        assertThat(positions.get(0), hasProperty("y", equalTo(2)));
    }

    @Test
    void shouldUpdateShipInDbAfterMovement() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 1, 1, battleId);
        MoveRequest request = getMoveRequest(2,2, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId);
        Optional<Ship> ship = shipRepository.findById(shipId);
        assertTrue(ship.isPresent());
        assertThat(ship.get(), hasProperty("movement", equalTo(true)));
    }

    @Test
    void shouldNotMoveIfShipIsAlreadyMoved() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId, true, false);
        placeShip(shipId, 1, 1, battleId);
        MoveRequest request = getMoveRequest(2,2, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotMoveIfDistanceIsLongerThan3() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 1, 1, battleId);
        MoveRequest request = getMoveRequest(5,5, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotAttackIfAttackedPositionIsEmpty() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 1, 1, battleId);
        MoveRequest request = getMoveRequest(2,1, MoveAction.ATTACK, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotAttackIfAttackedPositionIsHasNoShip() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 1, 1, battleId);
        makeEmptyPosition(2,1, battleId);
        MoveRequest request = getMoveRequest(2,1, MoveAction.ATTACK, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private void makeEmptyPosition(int x, int y, Long battleId) {
        Position position = new Position();
        position.setY(y);
        position.setX(x);
        position.setBattle(battleRepository.getReferenceById(battleId));
        positionRepository.save(position);
    }

    @Test
    void shouldNotAttackIfAttackedShipIsTooFarAway() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 1, 1, battleId);
        placeShip(addShip(expeditionId), 5, 5, battleId);
        MoveRequest request = getMoveRequest(5,5, MoveAction.ATTACK, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotAttackIfShipAlreadyAttacked() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId, true, true);
        placeShip(shipId, 1, 1, battleId);
        placeShip(addShip(expeditionId), 2, 1, battleId);
        MoveRequest request = getMoveRequest(2,1, MoveAction.ATTACK, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldAttackShip() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId, true, false);
        placeShip(shipId, 1, 1, battleId);
        placeShip(addShip(expeditionId), 2, 1, battleId);
        MoveRequest request = getMoveRequest(2,1, MoveAction.ATTACK, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(OK.value());
    }

    @Test
    void shouldUpdateShipInDbAfterAttack() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId, true, false);
        placeShip(shipId, 1, 1, battleId);
        placeShip(addShip(expeditionId), 2, 1, battleId);
        MoveRequest request = getMoveRequest(2,1, MoveAction.ATTACK, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId);
        Optional<Ship> ship = shipRepository.findById(shipId);
        assertTrue(ship.isPresent());
        assertThat(ship.get(), hasProperty("action", equalTo(true)));
    }

    @Test
    void shouldSaveDamagesInDb() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId, true, false);
        placeShip(shipId, 1, 1, battleId);
        Long attackedShipId = addShip(expeditionId, 2);
        placeShip(attackedShipId, 2, 1, battleId);
        MoveRequest request = getMoveRequest(2,1, MoveAction.ATTACK, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId);
        Optional<Ship> ship = shipRepository.findById(attackedShipId);
        assertTrue(ship.isPresent());
        assertThat(ship.get(), hasProperty("damaged", equalTo(true)));
        assertThat(ship.get(), hasProperty("hp", equalTo(1)));
    }

    @Test
    void shouldDestroyShip() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId, true, false);
        placeShip(shipId, 1, 1, battleId);
        Long attackedShipId = addShip(expeditionId, 1);
        placeShip(attackedShipId, 2, 1, battleId);
        MoveRequest request = getMoveRequest(2,1, MoveAction.ATTACK, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId);
        Optional<Ship> ship = shipRepository.findById(attackedShipId);
        assertTrue(ship.isPresent());
        assertThat(ship.get(), hasProperty("damaged", equalTo(true)));
        assertThat(ship.get(), hasProperty("destroyed", equalTo(true)));
        assertThat(ship.get(), hasProperty("hp", equalTo(0)));
    }

    @Test
    void shouldNotAttackIfShipIsDestroyed() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addDestroyedShip(expeditionId);
        placeShip(shipId, 1, 1, battleId);
        placeShip(addShip(expeditionId), 2, 2, battleId);
        MoveRequest request = getMoveRequest(2,2, MoveAction.ATTACK, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotMoveIfShipIsDestroyed() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addDestroyedShip(expeditionId);
        placeShip(shipId, 1, 1, battleId);
        MoveRequest request = getMoveRequest(2,2, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldRespondWith401ToEndTurnIfNoUserIdGiven() {
        when()
                .post(baseUrl + "/battle/{battleId}/turn/end", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToEndTurnIfBattleDoesNotExist() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldNotChangePhaseIfBattleIsFinished() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, true);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldChangePhaseIfBattleIsStarted() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, false);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 1, 1, battleId);
        placeShip(addShip(expeditionId, false, true), 1, 1, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
        List<Ship> ships = shipRepository.findAll();
        assertThat(ships, everyItem(hasProperty("movement", equalTo(false))));
        assertThat(ships, everyItem(hasProperty("action", equalTo(false))));
    }

    @Test
    void shouldNotChangePhaseIfThereIsShipThatIsNotPlaced() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, false, false);
        addShip(expeditionId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldChangePhaseIfAllShipsArePlaced() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, false, false);
        addPreparedShip(expeditionId);
        addPreparedShip(expeditionId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
    }

    @Test
    void shouldMakeBattleStartedInDb() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, false, false);
        addPreparedShip(expeditionId);
        addPreparedShip(expeditionId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId);
        Optional<Battle> battle = battleRepository.findById(battleId);
        assertTrue(battle.isPresent());
        assertThat(battle.get(), hasProperty("started", equalTo(true)));
    }

    @Test
    void shouldNotMoveEnemyShip() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addEnemyShip(expeditionId);
        MoveRequest request = getMoveRequest(1,1, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotAttackWithEnemyShip() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addEnemyShip(expeditionId);
        placeShip(shipId, 1, 1, battleId);
        placeShip(addShip(expeditionId), 2, 2, battleId);
        MoveRequest request = getMoveRequest(2,2, MoveAction.ATTACK, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldChangePhaseWithEnemyShips() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, false, false);
        addPreparedShip(expeditionId);
        addPreparedShip(expeditionId);
        addEnemyShip(expeditionId);
        addEnemyShip(expeditionId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
    }

    @Test
    void shouldChangePhaseWithEnemyShipsAndBattleStarted() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, false);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 2, 2, battleId);
        placeShip(addShip(expeditionId, false, true), 3, 3, battleId);
        placeShip(addEnemyShip(expeditionId), 5, 5, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
    }

    @Test
    void shouldAddEnemyShipsWhileStartingBattle() {
        Long expeditionId = addExpedition();
        addShip(expeditionId);
        addShipType(2);
        addResult(expeditionId, ResultType.BATTLE, false);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/battle", expeditionId)
        .then()
                .statusCode(OK.value());
        List<Ship> ships = shipRepository.findAll();
        assertThat(ships, hasSize(greaterThan(1)));
        assertThat(ships, hasItem(hasProperty("enemy", equalTo(true))));
    }

    private void addShipType(int rarity) {
        ShipType type = new ShipType();
        type.setRarity(rarity);
        type.setBaseSize(1);
        typeRepository.save(type);
    }

    @Test
    void shouldAssignPositionToEnemySHip() {
        Long expeditionId = addExpedition();
        addShip(expeditionId);
        addShipType(2);
        addResult(expeditionId, ResultType.BATTLE, false);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/battle", expeditionId);
        List<Ship> ships = shipRepository.findAll();
        assertThat(ships, hasSize(greaterThan(1)));
        assertThat(ships, hasItem(both(
                hasProperty("enemy", equalTo(true))).and(
                        hasProperty("position", notNullValue())
        )));
    }

    @Test
    void shouldNotMoveShipOutsideTheBoardWithNegativeCoordinate() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 1, 1, battleId);
        MoveRequest request = getMoveRequest(-1,1, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotMoveShipOutsideTheBoard() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 9, 9, battleId);
        MoveRequest request = getMoveRequest(11,9, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldAttackPlayerShipWithoutMovement() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, false);
        Long attackedShipId = addShip(expeditionId, true, true);
        placeShip(attackedShipId, 1, 1, battleId);
        placeShip(addEnemyShip(expeditionId), 1, 1, battleId);
        given()
                .header(getHeaderForUserId(userId))
                .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
                .then()
                .statusCode(OK.value())
                .body("$", hasSize(1));
        Optional<Ship> ship = shipRepository.findById(attackedShipId);
        assertTrue(ship.isPresent());
        assertThat(ship.get(), hasProperty("damaged", equalTo(true)));
        assertThat(ship.get(), hasProperty("hp", equalTo(2)));
    }

    @Test
    void shouldAttackPlayerShip() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, false);
        Long attackedShipId = addShip(expeditionId, true, true);
        placeShip(attackedShipId, 1, 1, battleId);
        placeShip(addEnemyShip(expeditionId), 3, 3, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(2));
        Optional<Ship> ship = shipRepository.findById(attackedShipId);
        assertTrue(ship.isPresent());
        assertThat(ship.get(), hasProperty("damaged", equalTo(true)));
        assertThat(ship.get(), hasProperty("hp", equalTo(2)));
    }

    @Test
    void shouldNotAddSecondBattleToDb() {
        Long expeditionId = addExpedition();
        addBattle(expeditionId);
        addResult(expeditionId, ResultType.BATTLE, false);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/battle", expeditionId);
        List<Battle> battles = battleRepository.findAll();
        assertThat(battles, hasSize(1));
    }

    @Test
    void shouldReturnExistingBattle() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId);
        addResult(expeditionId, ResultType.BATTLE, false);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/battle", expeditionId)
        .then()
                .statusCode(OK.value())
                .body("battleId", equalTo(battleId.intValue()));
    }

    @Test
    void shouldNotAttackPlayerShipIfNoneInRange() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, false);
        Long attackedShipId = addShip(expeditionId, true, true);
        placeShip(attackedShipId, 1, 1, battleId);
        placeShip(addEnemyShip(expeditionId), 9, 9, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(0));
        Optional<Ship> ship = shipRepository.findById(attackedShipId);
        assertTrue(ship.isPresent());
        assertThat(ship.get(), hasProperty("damaged", equalTo(false)));
    }

    @Test
    void shouldNotFinishBattleIfNotAllEnemyShipsAreDestroyed() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, false, BattleObjective.DEFEAT);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 1, 2, battleId);
        placeShip(addShip(expeditionId, false, true), 1, 3, battleId);
        placeShip(addEnemyShip(expeditionId), 9, 9, battleId);
        given()
                .header(getHeaderForUserId(userId))
                .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
        Battle battle = battleRepository.findById(battleId).get();
        assertFalse(battle.isFinished());
    }

    @Test
    void shouldNotFinishBattleIfOneOfTwoEnemyShipsAreDestroyed() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, false, BattleObjective.DEFEAT);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 1, 2, battleId);
        placeShip(addShip(expeditionId, false, true), 1, 3, battleId);
        placeShip(addEnemyShip(expeditionId), 9, 9, battleId);
        placeShip(addDestroyedEnemyShip(expeditionId), 9, 8, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
        Battle battle = battleRepository.findById(battleId).get();
        assertFalse(battle.isFinished());
    }

    @Test
    void shouldFinishBattleIfAllEnemyShipsAreDestroyed() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, false, BattleObjective.DEFEAT);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 1, 2, battleId);
        placeShip(addShip(expeditionId, false, true), 1, 3, battleId);
        placeShip(addDestroyedEnemyShip(expeditionId), 9, 9, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
        Battle battle = battleRepository.findById(battleId).get();
        assertTrue(battle.isFinished());
    }

    @Test
    void shouldFinishBattleIfAllEnemyShipsAreDestroyedForMultipleShips() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, false, BattleObjective.DEFEAT);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 1, 2, battleId);
        placeShip(addShip(expeditionId, false, true), 1, 3, battleId);
        placeShip(addDestroyedEnemyShip(expeditionId), 9, 9, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
        Battle battle = battleRepository.findById(battleId).get();
        assertTrue(battle.isFinished());
    }

    @Test
    void shouldFinishBattleInDefeatModeWithoutEnemyShips() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, false, BattleObjective.DEFEAT);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 1, 2, battleId);
        placeShip(addShip(expeditionId, false, true), 1, 3, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
        Battle battle = battleRepository.findById(battleId).get();
        assertTrue(battle.isFinished());
    }

    @Test
    void shouldNotFinishBattleIfBossIsNotDestroyed() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, false, BattleObjective.BOSS);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 1, 2, battleId);
        placeShip(addShip(expeditionId, false, true), 1, 3, battleId);
        placeShip(addEnemyShip(expeditionId), 9, 9, battleId);
        placeShip(addBoss(expeditionId, false), 9, 9, battleId);
        given()
                .header(getHeaderForUserId(userId))
                .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
                .then()
                .statusCode(OK.value());
        Battle battle = battleRepository.findById(battleId).get();
        assertFalse(battle.isFinished());
    }

    private Long addBoss(Long expeditionId, boolean destroyed) {
        Ship ship = new Ship();
        ship.setExpedition(expeditionRepository.getReferenceById(expeditionId));
        ship.setUserId(userId);
        ship.setSize(1);
        ship.setEnemy(true);
        ship.setBoss(true);
        ship.setMovement(true);
        ship.setAction(true);
        ship.setHp(1);
        ship.setRarity(2);
        ship.setStrength(1);
        ship.setHitRate(100);
        ship.setCriticalRate(0);
        ship.setDestroyed(destroyed);
        ship.setAttackRange(3);
        ship.setMovementRange(3);
        return shipRepository.save(ship).getId();
    }

    @Test
    void shouldFinishBattleIfBossIsDestroyed() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, false, BattleObjective.BOSS);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 1, 2, battleId);
        placeShip(addShip(expeditionId, false, true), 1, 3, battleId);
        placeShip(addEnemyShip(expeditionId), 9, 9, battleId);
        placeShip(addBoss(expeditionId, true), 9, 9, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
        Battle battle = battleRepository.findById(battleId).get();
        assertTrue(battle.isFinished());
    }

    @Test
    void shouldFinishBattleIfThereIsNoBoss() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, false, BattleObjective.BOSS);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 1, 2, battleId);
        placeShip(addShip(expeditionId, false, true), 1, 3, battleId);
        placeShip(addEnemyShip(expeditionId), 9, 9, battleId);
        given()
                .header(getHeaderForUserId(userId))
                .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
                .then()
                .statusCode(OK.value());
        Battle battle = battleRepository.findById(battleId).get();
        assertTrue(battle.isFinished());
    }

    @Test
    void shouldNotFinishBattleIfBattleHaveNotSeizedFlag() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true, false, BattleObjective.SEIZE);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 1, 2, battleId);
        placeShip(addShip(expeditionId, false, true), 1, 3, battleId);
        placeShip(addDestroyedEnemyShip(expeditionId), 9, 9, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
        Battle battle = battleRepository.findById(battleId).get();
        assertFalse(battle.isFinished());
    }

    @Test
    void shouldFinishBattleIfBattleHaveSeizedFlag() {
        Long expeditionId = addExpedition();
        Long battleId = addSeizedBattle(expeditionId);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 1, 2, battleId);
        placeShip(addShip(expeditionId, false, true), 1, 3, battleId);
        placeShip(addEnemyShip(expeditionId), 9, 9, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
        Battle battle = battleRepository.findById(battleId).get();
        assertTrue(battle.isFinished());
    }

    @Test
    void shouldNotFinishBattleIfThereAreMoreTurnsToSurvive() {
        Long expeditionId = addExpedition();
        Long battleId = addSurvivalBattle(expeditionId, 5, 1);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 1, 2, battleId);
        placeShip(addShip(expeditionId, false, true), 1, 3, battleId);
        placeShip(addEnemyShip(expeditionId), 9, 9, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
        Battle battle = battleRepository.findById(battleId).get();
        assertFalse(battle.isFinished());
    }

    @Test
    void shouldFinishBattleAfterSurvival() {
        Long expeditionId = addExpedition();
        Long battleId = addSurvivalBattle(expeditionId, 5, 5);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 1, 2, battleId);
        placeShip(addShip(expeditionId, false, true), 1, 3, battleId);
        placeShip(addEnemyShip(expeditionId), 9, 9, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
        Battle battle = battleRepository.findById(battleId).get();
        assertTrue(battle.isFinished());
    }

    @Test
    void shouldFinishSurvivalBattleIfAllEnemyShipsAreDestroyedEvenIfTurnCountIsTooLow() {
        Long expeditionId = addExpedition();
        Long battleId = addSurvivalBattle(expeditionId, 5, 2);
        placeShip(addShip(expeditionId, true, true), 1, 1, battleId);
        placeShip(addShip(expeditionId, true, false), 1, 2, battleId);
        placeShip(addShip(expeditionId, false, true), 1, 3, battleId);
        placeShip(addDestroyedEnemyShip(expeditionId), 9, 9, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId)
        .then()
                .statusCode(OK.value());
        Battle battle = battleRepository.findById(battleId).get();
        assertTrue(battle.isFinished());
    }

    @Test
    void shouldChangeShipPositionInDbIfShipNorTargetHasTerrain() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 1, 1, battleId);
        MoveRequest request = getMoveRequest(2,2, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId);
        List<Position> positions = positionRepository.findAll();
        assertThat(positions, hasItem(
                both(hasProperty("x", equalTo(2)))
                        .and(
                                both(hasProperty("y", equalTo(2)))
                                        .and(both(hasProperty("ship", hasProperty("id", equalTo(shipId))))
                                                .and(hasProperty("terrain", nullValue())))
                        )
        ));
    }


    private void placeShipWithTerrain(Long shipId, int x, int y, Long battleId, Long terrainId) {
        Position position = new Position();
        position.setY(y);
        position.setX(x);
        position.setShip(shipRepository.getReferenceById(shipId));
        position.setBattle(battleRepository.getReferenceById(battleId));
        position.setTerrain(terrainRepository.getReferenceById(terrainId));
        positionRepository.save(position);
    }
    private void placeTerrain(Long terrainId, int x, int y, Long battleId) {
        Position position = new Position();
        position.setY(y);
        position.setX(x);
        position.setTerrain(terrainRepository.getReferenceById(terrainId));
        position.setBattle(battleRepository.getReferenceById(battleId));
        positionRepository.save(position);
    }

    @Test
    void shouldChangeShipPositionInDbIfShipHasTerrain() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        Long terrainId = addTerrain();
        placeShipWithTerrain(shipId, 1, 1, battleId, terrainId);
        MoveRequest request = getMoveRequest(2,2, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId);
        List<Position> positions = positionRepository.findAll();
        assertThat(positions, hasItem(
                both(hasProperty("x", equalTo(2)))
                        .and(
                                both(hasProperty("y", equalTo(2)))
                                        .and(both(hasProperty("ship", hasProperty("id", equalTo(shipId))))
                                                .and(hasProperty("terrain", nullValue())))
                        )
        ));
        assertThat(positions, hasItem(
                both(hasProperty("x", equalTo(1)))
                        .and(
                                both(hasProperty("y", equalTo(1)))
                                        .and(both(hasProperty("terrain", hasProperty("id", equalTo(terrainId))))
                                                .and(hasProperty("ship", nullValue())))
                        )
        ));
    }

    private Long addTerrain() {
        return addTerrain(false);
    }

    private Long addTerrain(boolean blocking) {
        TerrainType terrainType = new TerrainType();
        terrainType.setMove(2);
        terrainType.setBlocked(blocking);
        return terrainRepository.save(terrainType).getId();
    }

    @Test
    void shouldChangeShipPositionInDbIfTargetHasTerrain() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        Long terrainId = addTerrain();
        placeShip(shipId, 1, 1, battleId);
        placeTerrain(terrainId, 2, 2, battleId);
        MoveRequest request = getMoveRequest(2,2, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId);
        List<Position> positions = positionRepository.findAll();
        assertThat(positions, hasItem(
                both(hasProperty("x", equalTo(2)))
                        .and(
                                both(hasProperty("y", equalTo(2)))
                                        .and(both(hasProperty("ship", hasProperty("id", equalTo(shipId))))
                                                .and(hasProperty("terrain", hasProperty("id", equalTo(terrainId)))))
                        )
        ));
    }

    @Test
    void shouldChangeShipPositionInDbIfBothShipAndTargetHasTerrain() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        Long terrain1Id = addTerrain();
        Long terrain2Id = addTerrain();
        placeShipWithTerrain(shipId, 1, 1, battleId, terrain1Id);
        placeTerrain(terrain2Id, 2, 2, battleId);
        MoveRequest request = getMoveRequest(2,2, MoveAction.MOVE, shipId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(baseUrl + "/battle/{battleId}/move", battleId);
        List<Position> positions = positionRepository.findAll();
        assertThat(positions, hasItem(
                both(hasProperty("x", equalTo(2)))
                        .and(
                                both(hasProperty("y", equalTo(2)))
                                        .and(both(hasProperty("ship", hasProperty("id", equalTo(shipId))))
                                                .and(hasProperty("terrain", hasProperty("id", equalTo(terrain2Id)))))
                        )
        ));
        assertThat(positions, hasItem(
                both(hasProperty("x", equalTo(1)))
                        .and(
                                both(hasProperty("y", equalTo(1)))
                                        .and(both(hasProperty("terrain", hasProperty("id", equalTo(terrain1Id))))
                                                .and(hasProperty("ship", nullValue())))
                        )
        ));
    }

    @Test
    void shouldChangeEnemyShipPositionInDbIfShipNorTargetHasTerrain() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 2, 0, battleId);
        Long barrierId = addTerrain(true);
        placeTerrain(barrierId, 1, 2, battleId);
        placeTerrain(barrierId, 0, 1, battleId);
        Long enemyShipId = addEnemyShip(expeditionId);
        placeShip(enemyShipId, 1, 3, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId);
        List<Position> positions = positionRepository.findAll();
        assertThat(positions, hasItem(
                both(hasProperty("x", equalTo(2)))
                        .and(
                                both(hasProperty("y", equalTo(3)))
                                        .and(both(hasProperty("ship", hasProperty("id", equalTo(enemyShipId))))
                                                .and(hasProperty("terrain", nullValue())))
                        )
        ));
    }

    @Test
    void shouldChangeEnemyShipPositionInDbIfShipHasTerrain() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 2, 0, battleId);
        Long barrierId = addTerrain(true);
        placeTerrain(barrierId, 1, 2, battleId);
        placeTerrain(barrierId, 0, 1, battleId);
        Long terrainId = addTerrain();
        Long enemyShipId = addEnemyShip(expeditionId);
        placeShipWithTerrain(enemyShipId, 1, 3, battleId, terrainId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId);
        List<Position> positions = positionRepository.findAll();
        assertThat(positions, hasItem(
                both(hasProperty("x", equalTo(2)))
                        .and(
                                both(hasProperty("y", equalTo(3)))
                                        .and(both(hasProperty("ship", hasProperty("id", equalTo(enemyShipId))))
                                                .and(hasProperty("terrain", nullValue())))
                        )
        ));
        assertThat(positions, hasItem(
                both(hasProperty("x", equalTo(1)))
                        .and(
                                both(hasProperty("y", equalTo(3)))
                                        .and(both(hasProperty("terrain", hasProperty("id", equalTo(terrainId))))
                                                .and(hasProperty("ship", nullValue())))
                        )
        ));
    }

    @Test
    void shouldChangeEnemyShipPositionInDbIfTargetHasTerrain() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 2, 0, battleId);
        Long barrierId = addTerrain(true);
        placeTerrain(barrierId, 1, 2, battleId);
        placeTerrain(barrierId, 0, 1, battleId);
        Long terrainId = addTerrain();
        Long enemyShipId = addEnemyShip(expeditionId);
        placeShip(enemyShipId, 1, 3, battleId);
        placeTerrain(terrainId, 2, 3, battleId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId);
        List<Position> positions = positionRepository.findAll();
        assertThat(positions, hasItem(
                both(hasProperty("x", equalTo(2)))
                        .and(
                                both(hasProperty("y", equalTo(3)))
                                        .and(both(hasProperty("ship", hasProperty("id", equalTo(enemyShipId))))
                                                .and(hasProperty("terrain", hasProperty("id", equalTo(terrainId)))))
                        )
        ));
    }

    @Test
    void shouldChangeEnemyShipPositionInDbIfBothShipAndTargetHasTerrain() {
        Long expeditionId = addExpedition();
        Long battleId = addBattle(expeditionId, true);
        Long shipId = addShip(expeditionId);
        placeShip(shipId, 2, 0, battleId);
        Long barrierId = addTerrain(true);
        placeTerrain(barrierId, 1, 2, battleId);
        placeTerrain(barrierId, 0, 1, battleId);
        Long terrain1Id = addTerrain();
        Long terrain2Id = addTerrain();
        Long enemyShipId = addEnemyShip(expeditionId);
        placeShipWithTerrain(enemyShipId, 1, 3, battleId, terrain1Id);
        placeTerrain(terrain2Id, 2, 3, battleId);
        given()
                .header(getHeaderForUserId(userId))
                .when()
                .post(baseUrl + "/battle/{battleId}/turn/end", battleId);
        List<Position> positions = positionRepository.findAll();
        assertThat(positions, hasItem(
                both(hasProperty("x", equalTo(2)))
                        .and(
                                both(hasProperty("y", equalTo(3)))
                                        .and(both(hasProperty("ship", hasProperty("id", equalTo(enemyShipId))))
                                                .and(hasProperty("terrain", hasProperty("id", equalTo(terrain2Id)))))
                        )
        ));
        assertThat(positions, hasItem(
                both(hasProperty("x", equalTo(1)))
                        .and(
                                both(hasProperty("y", equalTo(3)))
                                        .and(both(hasProperty("terrain", hasProperty("id", equalTo(terrain1Id))))
                                                .and(hasProperty("ship", nullValue())))
                        )
        ));
    }
}