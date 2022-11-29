package io.github.xpakx.habitgame.discovery;

import io.github.xpakx.habitgame.expedition.*;
import io.github.xpakx.habitgame.expedition.dto.ActionRequest;
import io.github.xpakx.habitgame.island.Island;
import io.github.xpakx.habitgame.island.IslandRepository;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DiscoveryControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;

    @Autowired
    IslandRepository islandRepository;
    @Autowired
    ExpeditionResultRepository resultRepository;
    @Autowired
    ExpeditionRepository expeditionRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        resultRepository.deleteAll();
        expeditionRepository.deleteAll();
        islandRepository.deleteAll();
    }

    private Header getHeaderForUserId(Long userId) {
        return new Header("id", userId.toString());
    }

    @Test
    void shouldRespondWith401ToRevealIslandIfNoUserIdIsGiven() {
        when()
                .get(baseUrl + "/expedition/{expeditionId}/island", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToRevealIslandIfExpeditionDoesNotExist() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/island", 1L)
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
    void shouldNotRevealIslandForCompletedResult() {
        Long expeditionId = addExpedition();
        addResult(expeditionId, ResultType.ISLAND, true);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/island", expeditionId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotRevealIslandForNonIslandResultType() {
        Long expeditionId = addExpedition();
        addResult(expeditionId, ResultType.BATTLE, false);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/island", expeditionId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldRevealIsland() {
        Long expeditionId = addExpedition();
        addResult(expeditionId, ResultType.ISLAND, false);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/island", expeditionId)
        .then()
                .statusCode(OK.value());
    }

    @Test
    void shouldCompleteExpeditionResultAfterRevealingAnIsland() {
        Long expeditionId = addExpedition();
        addResult(expeditionId, ResultType.ISLAND, false);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/island", expeditionId);
        Optional<ExpeditionResult> result = resultRepository.findByExpeditionId(expeditionId);
        assertTrue(result.isPresent());
        assertThat(result.get(), hasProperty("completed", equalTo(true)));
    }

    @Test
    void shouldAddNewIslandToDb() {
        Long expeditionId = addExpedition();
        addResult(expeditionId, ResultType.ISLAND, false);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/island", expeditionId);
        List<Island> islands = islandRepository.findAll();
        assertThat(islands, hasSize(1));
    }
}