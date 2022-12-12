package io.github.xpakx.habitgame.battle;

import io.github.xpakx.habitgame.expedition.*;
import io.github.xpakx.habitgame.island.Island;
import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
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
                .get(baseUrl + "/expedition/{expeditionId}/island", expeditionId);
        List<Battle> battles = battleRepository.findAll();
        assertThat(battles, hasSize(1));
    }

}