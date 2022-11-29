package io.github.xpakx.habitgame.island;

import io.github.xpakx.habitgame.island.dto.NamingIslandRequest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IslandControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;

    @Autowired
    IslandRepository islandRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        islandRepository.deleteAll();
    }

    private Header getHeaderForUserId(Long userId) {
        return new Header("id", userId.toString());
    }


    @Test
    void shouldRespondWith401ToGetIslandsIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/island/all")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWithEmptyIslandList() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/island/all")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(0));
    }

    @Test
    void shouldReturnListOfIslandsForUser() {
        addIsland(userId, "island1");
        addIsland(userId, "island2");
        addIsland(userId+1, "island3");
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/island/all")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(2))
                .body("name", hasItem("island1"))
                .body("name", hasItem("island2"))
                .body("name", not(hasItem("island3")));
    }

    private Long addIsland(long userId, String islandName) {
        Island island = new Island();
        island.setName(islandName);
        island.setUserId(userId);
        return islandRepository.save(island).getId();
    }

    @Test
    void shouldRespondWith401ToNameIslandIfNoUserIdGiven() {
        when()
                .post(baseUrl + "/island/{islandId}/name", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToNameIslandIfIslandDoesNotExist() {
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(getNameRequest("island"))
        .when()
                .post(baseUrl + "/island/{islandId}/name", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private NamingIslandRequest getNameRequest(String name) {
        NamingIslandRequest request = new NamingIslandRequest();
        request.setName(name);
        return request;
    }

    @Test
    void shouldNameIsland() {
        Long islandId = addIsland(userId, "None");
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(getNameRequest("island"))
        .when()
                .post(baseUrl + "/island/{islandId}/name", islandId)
        .then()
                .statusCode(OK.value())
                .body("islandName", equalTo("island"));
    }

    @Test
    void shouldChangeIslandName() {
        Long islandId = addIsland(userId, "None");
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(getNameRequest("island"))
        .when()
                .post(baseUrl + "/island/{islandId}/name", islandId);
        Optional<Island> island = islandRepository.findById(islandId);
        assertTrue(island.isPresent());
        assertThat(island.get(), hasProperty("name", equalTo("island")));
    }
}