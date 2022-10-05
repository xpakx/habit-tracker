package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.clients.GamificationPublisher;
import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HabitCompletionControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;

    @Autowired
    HabitRepository habitRepository;
    @Autowired
    HabitCompletionRepository completionRepository;

    @MockBean
    GamificationPublisher publisher;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        completionRepository.deleteAll();
        habitRepository.deleteAll();
    }

    private Header getHeaderForUserId(Long userId) {
        return new Header("id", userId.toString());
    }

    private Long addNewHabit(String name) {
        return addNewHabit(name, userId);
    }

    private Long addNewHabit(String name, Long userId) {
        Habit habit = new Habit();
        habit.setUserId(userId);
        habit.setName(name);
        return habitRepository.save(habit).getId();
    }

    @Test
    void shouldRespondWith401ToCompleteHabitIfNoUserIdGiven() {
        when()
                .post(baseUrl + "/habit/{habitId}/completion", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToCompleteHabitIfHabitDoesNotExist() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/habit/{habitId}/completion", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }
}