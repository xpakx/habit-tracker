package io.github.xpakx.habitgame.expedition;

import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExpeditionControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;

    @Autowired
    ExpeditionRepository expeditionRepository;
    @Autowired
    ExpeditionResultRepository resultRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        resultRepository.deleteAll();
        expeditionRepository.deleteAll();
    }

    private Header getHeaderForUserId(Long userId) {
        return new Header("id", userId.toString());
    }

    @Test
    void shouldRespondWith401ToGetExpeditionsIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/expedition/active")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWithEmptyExpeditionList() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/active")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(0));
    }

    @Test
    void shouldReturnListOfExpeditionsForUser() {
        addExpedition(userId);
        addExpedition(userId);
        addExpedition(userId+1);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/active")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(2));
    }

    private Long addExpedition(long userId) {
        return addExpedition(userId, null);
    }

    private Long addExpedition(long userId, LocalDateTime end) {
        Expedition expedition = new Expedition();
        expedition.setUserId(userId);
        expedition.setFinished(false);
        expedition.setEnd(end);
        return expeditionRepository.save(expedition).getId();
    }

    @Test
    void shouldRespondWith401ToGetExpeditionResultIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/expedition/{expeditionId}/result", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToGetExpeditionResultIfExpeditionDoesNotExist() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/result", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }
    @Test
    void shouldNotGenerateResultIfExpeditionNotFinished() {
        Long expeditionId = addExpedition(userId, LocalDateTime.now().plusDays(1));
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/result", expeditionId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }
    @Test
    void shouldNotGenerateSecondResultForExpedition() {
        Long expeditionId = addExpedition(userId, LocalDateTime.now().minusDays(1));
        addResult(expeditionId, ResultType.NONE);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/result", expeditionId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private void addResult(Long expeditionId, ResultType type) {
        ExpeditionResult result = new ExpeditionResult();
        result.setExpedition(expeditionRepository.getReferenceById(expeditionId));
        result.setCompleted(false);
        result.setType(type);
        resultRepository.save(result);
    }
    @Test
    void shouldGenerateExpeditionResult() {
        Long expeditionId = addExpedition(userId, LocalDateTime.now().minusDays(1));
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/expedition/{expeditionId}/result", expeditionId)
        .then()
                .statusCode(OK.value());
        List<ExpeditionResult> results = resultRepository.findAll();
        assertThat(results, hasSize(1));
    }
}