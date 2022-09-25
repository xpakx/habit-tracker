package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.HabitRequest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpStatus.*;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HabitControllerTest {
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

    @Test
    void shouldRespondWith401ToAddHabitIfNoUserIdGiven() {
        when()
                .post(baseUrl + "/habit")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotAddNewHabitToNonexistentContext() {
        HabitRequest request = getHabitRequest("habit1", "trigger1", 3L);
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/habit")
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private Header getHeaderForUserId(Long userId) {
        return new Header("id", userId.toString());
    }

    @Test
    void shouldAddNewHabit() {
        HabitRequest request = getHabitRequest("habit1", "trigger1", null);
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId))
        .when()
                .post(baseUrl + "/habit")
        .then()
                .statusCode(CREATED.value())
                .body("name", equalTo(request.getName()));
        List<Habit> habits = habitRepository.findAll();
        assertThat(habits, hasSize(1));
    }

    private HabitRequest getHabitRequest(String name, String triggerName, Long contextId) {
        HabitRequest request = new HabitRequest();
        request.setName(name);
        request.setContextId(contextId);
        request.setTriggerName(triggerName);
        return request;
    }
}