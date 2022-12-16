package io.github.xpakx.habitgame.battle;

import io.github.xpakx.habitgame.battle.dto.MoveAction;
import io.github.xpakx.habitgame.battle.dto.MoveRequest;
import io.github.xpakx.habitgame.expedition.*;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        positionRepository.deleteAll();
        battleRepository.deleteAll();
        resultRepository.deleteAll();
        shipRepository.deleteAll();
        expeditionRepository.deleteAll();
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
        Battle battle = new Battle();
        battle.setStarted(started);
        battle.setFinished(finished);
        battle.setExpedition(expeditionRepository.getReferenceById(expeditionId));
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
        Ship ship = new Ship();
        ship.setExpedition(expeditionRepository.getReferenceById(expeditionId));
        ship.setUserId(userId);
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
}