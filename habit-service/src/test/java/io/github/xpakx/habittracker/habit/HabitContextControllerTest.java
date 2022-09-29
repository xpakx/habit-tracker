package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.HabitContextRequest;
import io.github.xpakx.habittracker.habit.dto.HabitUpdateRequest;
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

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HabitContextControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;


    @Autowired
    HabitRepository habitRepository;
    @Autowired
    HabitContextRepository contextRepository;
    @Autowired
    HabitTriggerRepository triggerRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        habitRepository.deleteAll();
        contextRepository.deleteAll();
        triggerRepository.deleteAll();
    }

    private Header getHeaderForUserId(Long userId) {
        return new Header("id", userId.toString());
    }

    @Test
    void shouldRespondWith401ToAddContextIfNoUserIdGiven() {
        when()
                .post(baseUrl + "/context")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldAddNewContext() {
        HabitContextRequest request = getContextRequest("new context");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/context")
        .then()
                .statusCode(CREATED.value())
                .body("name", equalTo(request.getName()));
        List<HabitContext> contexts = contextRepository.findAll();
        assertThat(contexts, hasSize(1));
    }

    private HabitContextRequest getContextRequest(String name) {
        HabitContextRequest request = new HabitContextRequest();
        request.setName(name);
        return request;
    }

    @Test
    void shouldRespondWith401ToUpdateContextIfNoUserIdGiven() {
        when()
                .put(baseUrl + "/context/{contextId}", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWithNotFoundIfHContextWithGivenIdDoesNotExist() {
        HabitContextRequest request = getContextRequest("new name");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId))
        .when()
                .put(baseUrl + "/context/{contextId}", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldRespondWithNotFoundIfContextForGivenUserDoesNotExist() {
        HabitContextRequest request = getContextRequest("new name");
        Long contextId = addNewContext("old name");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId+1))
        .when()
                .put(baseUrl + "/context/{contextId}", contextId)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private Long addNewContext(String name) {
        HabitContext context = new HabitContext();
        context.setUserId(userId);
        context.setName(name);
        return contextRepository.save(context).getId();
    }

    @Test
    void shouldUpdateHabit() {
        HabitContextRequest request = getContextRequest("new name");
        Long contextId = addNewContext("old name");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId))
        .when()
                .put(baseUrl + "/context/{contextId}", contextId)
        .then()
                .statusCode(OK.value())
                .body("name", equalTo(request.getName()));
    }

}