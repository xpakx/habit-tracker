package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.TriggerUpdateRequest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HabitTriggerControllerTest {
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
    void shouldRespondWith401ToUpdateHabitTriggerIfNoUserIdGiven() {
        when()
                .put(baseUrl + "/habit/{habitId}/trigger", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWithNotFoundIfHabitTriggerDoesNotExist() {
        TriggerUpdateRequest request = getTriggerRequest("new name");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId))
        .when()
                .put(baseUrl + "/habit/{habitId}/trigger", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldRespondWithNotFoundIfHabitForGivenUserDoesNotExist() {
        TriggerUpdateRequest request = getTriggerRequest("new name");
        Long habitId = addNewHabit("habit", "name");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId+1))
        .when()
                .put(baseUrl + "/habit/{habitId}/trigger", habitId)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldUpdateTrigger() {
        TriggerUpdateRequest request = getTriggerRequest("new name");
        Long habitId = addNewHabit("habit", "old name");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId))
        .when()
                .put(baseUrl + "/habit/{habitId}/trigger", habitId)
        .then()
                .statusCode(OK.value())
                .body("name", equalTo(request.getName()));
    }

    private Long addNewHabit(String habitName, String triggerName) {
        Habit habit = new Habit();
        habit.setUserId(userId);
        habit.setName(habitName);
        HabitTrigger trigger = new HabitTrigger();
        trigger.setHabit(habit);
        trigger.setName(triggerName);
        habit.setTrigger(trigger);
        return habitRepository.save(habit).getId();
    }


    private TriggerUpdateRequest getTriggerRequest(String name) {
        TriggerUpdateRequest request = new TriggerUpdateRequest();
        request.setName(name);
        return request;
    }
}