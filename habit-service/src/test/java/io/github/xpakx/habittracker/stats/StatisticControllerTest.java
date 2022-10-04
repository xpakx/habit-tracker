package io.github.xpakx.habittracker.stats;

import io.github.xpakx.habittracker.habit.*;
import io.github.xpakx.habittracker.habit.dto.HabitContextRequest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import net.bytebuddy.asm.Advice;
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
    @Autowired
    HabitCompletionRepository completionRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        completionRepository.deleteAll();
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

    private Long addNewHabit(String name, Long contextId, Long userId) {
        Habit habit = new Habit();
        habit.setUserId(userId);
        habit.setName(name);
        habit.setContext(contextRepository.getReferenceById(contextId));
        return habitRepository.save(habit).getId();
    }

    private Long addNewHabit(String name, Long contextId) {
        return addNewHabit(name, contextId, userId);
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

    @Test
    void shouldRespondEmptyListIfContextHasOnlyCompletionsOlderThanYear() {
        Long contextId = addNewContext("context");
        LocalDateTime date = LocalDateTime.now().minusYears(1).minusDays(2);
        Long habit1Id = addNewHabit("habit1", contextId);
        Long habit2Id = addNewHabit("habit2", contextId);
        Long habit3Id = addNewHabit("habit3", contextId, userId+1);
        completeHabit(habit1Id, date);
        completeHabit(habit1Id, date);
        completeHabit(habit2Id, date);
        completeHabit(habit3Id, date);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/context/{contextId}/stats", contextId)
        .then()
                .statusCode(OK.value())
                .body("days", hasSize(0))
                .body("completions", equalTo(0));
    }

    private void completeHabit(Long habitId, LocalDateTime date) {
        completeHabit(habitId, date, userId);
    }

    private void completeHabit(Long habit1Id, LocalDateTime date, Long userId) {
        HabitCompletion completion = new HabitCompletion();
        completion.setUserId(userId);
        completion.setDate(date);
        completion.setHabit(habitRepository.getReferenceById(habit1Id));
        completionRepository.save(completion);
    }

    @Test
    void shouldRespondWithStatsForContext() {
        Long contextId = addNewContext("context");
        LocalDateTime date = LocalDateTime.now().minusMonths(5);
        Long habit1Id = addNewHabit("habit1", contextId);
        Long habit2Id = addNewHabit("habit2", contextId);
        Long habitThatBelongToDifferentUser = addNewHabit("habit3", contextId, userId+1);
        completeHabit(habit1Id, date);
        completeHabit(habit1Id, date.minusDays(3));
        completeHabit(habit1Id, date.minusYears(3));
        completeHabit(habit2Id, date);
        completeHabit(habitThatBelongToDifferentUser, date, userId+1);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/context/{contextId}/stats", contextId)
        .then()
                .statusCode(OK.value())
                .body("days", hasSize(2))
                .body("completions", equalTo(3));
    }


}