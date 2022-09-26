package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.HabitRequest;
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

import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
                .log().body()
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

    @Test
    void shouldRespondWith401ToUpdateHabitIfNoUserIdGiven() {
        when()
                .put(baseUrl + "/habit/{habitId}", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWithNotFoundIfHabitWithGivenIdDoesNotExist() {
        HabitUpdateRequest request = getUpdateHabitRequest("new name");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId))
        .when()
                .put(baseUrl + "/habit/{habitId}", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldRespondWithNotFoundIfHabitForGivenUserDoesNotExist() {
        HabitUpdateRequest request = getUpdateHabitRequest("new name");
        Long habitId = addNewHabit("old name");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId+1))
        .when()
                .put(baseUrl + "/habit/{habitId}", habitId)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private Long addNewHabit(String name) {
        return addNewHabit(name, LocalDateTime.now());
    }

    private Long addNewHabit(String name, LocalDateTime date) {
        Habit habit = new Habit();
        habit.setUserId(userId);
        habit.setName(name);
        habit.setNextDue(date);
        return habitRepository.save(habit).getId();
    }


    private HabitUpdateRequest getUpdateHabitRequest(String name) {
        HabitUpdateRequest request = new HabitUpdateRequest();
        request.setName(name);
        return request;
    }

    @Test
    void shouldUpdateHabit() {
        HabitUpdateRequest request = getUpdateHabitRequest("new name");
        Long habitId = addNewHabit("old name");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId))
        .when()
                .put(baseUrl + "/habit/{habitId}", habitId)
        .then()
                .statusCode(OK.value())
                .body("name", equalTo(request.getName()));
    }

    @Test
    void shouldRespondWith401ToGetHabitsForDayIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/habit/date")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnHabitsForDate() {
        HabitUpdateRequest request = getUpdateHabitRequest("new name");
        LocalDateTime date = LocalDateTime.now().minusDays(5);
        addNewHabit("first", date);
        addNewHabit("second", date);
        addNewHabit("third", date);
        addNewHabit("fourth", date.minusDays(1));
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId))
                .queryParam("date", date.toLocalDate().toString())
                .log().uri()
        .when()
                .get(baseUrl + "/habit/date")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(3))
                .body("name", hasItem(equalTo("first")))
                .body("name", hasItem(equalTo("second")))
                .body("name", hasItem(equalTo("third")))
                .body("name", not(hasItem(equalTo("fourth"))));
    }

    @Test
    void shouldRespondWith401ToGetDailyHabitsIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/habit/daily")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnDailyHabits() {
        HabitUpdateRequest request = getUpdateHabitRequest("new name");
        LocalDateTime date = LocalDateTime.now();
        addNewHabit("first", date);
        addNewHabit("second", date);
        addNewHabit("third", date);
        addNewHabit("fourth", date.minusDays(1));
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/habit/daily")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(3))
                .body("name", hasItem(equalTo("first")))
                .body("name", hasItem(equalTo("second")))
                .body("name", hasItem(equalTo("third")))
                .body("name", not(hasItem(equalTo("fourth"))));
    }

    @Test
    void shouldRespondWith401ToGetAllHabitsIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/habit")
                .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnAllHabits() {
        HabitUpdateRequest request = getUpdateHabitRequest("new name");
        LocalDateTime date = LocalDateTime.now();
        addNewHabit("first", date);
        addNewHabit("second", date.minusDays(1));
        addNewHabit("third", date.minusDays(5));
        addNewHabit("fourth", date.minusDays(100));
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/habit")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(4))
                .body("name", hasItem(equalTo("first")))
                .body("name", hasItem(equalTo("second")))
                .body("name", hasItem(equalTo("third")))
                .body("name", hasItem(equalTo("fourth")));
    }
}