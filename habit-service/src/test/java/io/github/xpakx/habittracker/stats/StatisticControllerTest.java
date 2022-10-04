package io.github.xpakx.habittracker.stats;

import io.github.xpakx.habittracker.habit.*;
import io.github.xpakx.habittracker.habit.dto.HabitContextRequest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StatisticControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;


    @Autowired
    HabitRepository habitRepository;
    @Autowired
    HabitContextRepository contextRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        habitRepository.deleteAll();
        contextRepository.deleteAll();
    }

    private Header getHeaderForUserId(Long userId) {
        return new Header("id", userId.toString());
    }

    private Long addNewContext(String name) {
        return addNewContext(name, userId);
    }

    private Long addNewContext(String name, Long owner) {
        HabitContext context = new HabitContext();
        context.setUserId(owner);
        context.setName(name);
        return contextRepository.save(context).getId();

    }

    private void addNewHabit(String name, LocalDateTime date, Long contextId) {
        Habit habit = new Habit();
        habit.setUserId(userId);
        habit.setName(name);
        habit.setNextDue(date);
        habit.setContext(contextRepository.getReferenceById(contextId));
        habitRepository.save(habit);
    }

    @Test
    void shouldRespondWith401ToGetStatsForContextIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/context/{contextId}/stats", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondEmptyListIfContextDoesNotExist() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/context/{contextId}/stats", 1L)
        .then()
                .statusCode(OK.value())
                .body("days", hasSize(0))
                .body("completions", equalTo(0));
    }

    @Test
    void shouldRespondEmptyListIfContextIsEmpty() {
        Long contextId = addNewContext("context");
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/context/{contextId}/stats", contextId)
        .then()
                .statusCode(OK.value())
                .body("days", hasSize(0))
                .body("completions", equalTo(0));
    }


}