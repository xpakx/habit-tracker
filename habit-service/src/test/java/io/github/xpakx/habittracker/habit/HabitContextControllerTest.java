package io.github.xpakx.habittracker.habit;

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
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
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
        return addNewContext(name, userId);
    }

    private Long addNewContext(String name, Long owner) {
        HabitContext context = new HabitContext();
        context.setUserId(owner);
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

    @Test
    void shouldRespondWith401ToGetHabitsForDayAndContextIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/context/{contextId}/habit/date", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnHabitsForDateAndContext() {
        LocalDateTime date = LocalDateTime.now().minusDays(5);
        Long contextId = addNewContext("context");
        Long otherContextId = addNewContext("second context");
        addNewHabit("first", date, contextId);
        addNewHabit("second", date, contextId);
        addNewHabit("third", date, contextId);
        addNewHabit("fourth", date, otherContextId);
        addNewHabit("fifth", date.minusDays(1), contextId);
        given()
                .header(getHeaderForUserId(userId))
                .queryParam("date", date.toLocalDate().toString())
                .log().uri()
        .when()
                .get(baseUrl + "/context/{contextId}/habit/date", contextId)
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(3))
                .body("name", hasItem(equalTo("first")))
                .body("name", hasItem(equalTo("second")))
                .body("name", hasItem(equalTo("third")))
                .body("name", not(hasItem(equalTo("fourth"))))
                .body("name", not(hasItem(equalTo("fifth"))));
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
    void shouldRespondWith401ToGetDailyHabitsForContextIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/context/{contextId}/habit/daily", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnDailyHabitsForContext() {
        LocalDateTime date = LocalDateTime.now();
        Long contextId = addNewContext("context");
        Long otherContextId = addNewContext("second context");
        addNewHabit("first", date, contextId);
        addNewHabit("second", date, contextId);
        addNewHabit("third", date,contextId);
        addNewHabit("fourth", date, otherContextId);
        addNewHabit("fifth", date.minusDays(1), contextId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/context/{contextId}/habit/daily", contextId)
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(3))
                .body("name", hasItem(equalTo("first")))
                .body("name", hasItem(equalTo("second")))
                .body("name", hasItem(equalTo("third")))
                .body("name", not(hasItem(equalTo("fourth"))))
                .body("name", not(hasItem(equalTo("fifth"))));
    }

    @Test
    void shouldRespondWith401ToGetHabitsForContextIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/context/{contextId}/habit", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnHabitsForContext() {
        LocalDateTime date = LocalDateTime.now();
        Long contextId = addNewContext("context");
        Long otherContextId = addNewContext("second context");
        addNewHabit("first", date, contextId);
        addNewHabit("second", date, contextId);
        addNewHabit("third", date,contextId);
        addNewHabit("fourth", date, otherContextId);
        addNewHabit("fifth", date.minusDays(1), contextId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/context/{contextId}/habit", contextId)
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(4))
                .body("name", hasItem(equalTo("first")))
                .body("name", hasItem(equalTo("second")))
                .body("name", hasItem(equalTo("third")))
                .body("name", not(hasItem(equalTo("fourth"))))
                .body("name", hasItem(equalTo("fifth")));
    }

    @Test
    void shouldRespondWith401ToGetContextsIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/context/all")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnContexts() {
        addNewContext("first");
        addNewContext("second");
        addNewContext("third", userId+1);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/context/all")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(2))
                .body("name", hasItem(equalTo("first")))
                .body("name", hasItem(equalTo("second")))
                .body("name", not(hasItem(equalTo("third"))));
    }
}