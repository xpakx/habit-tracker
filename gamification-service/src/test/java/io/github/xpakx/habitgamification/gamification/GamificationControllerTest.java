package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.badge.AchievementRepository;
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
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GamificationControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;

    @Autowired
    ExpEntryRepository expRepository;
    @Autowired
    AchievementRepository achievementRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        achievementRepository.deleteAll();
        expRepository.deleteAll();
    }

    private Header getHeaderForUserId(Long userId) {
        return new Header("id", userId.toString());
    }

    @Test
    void shouldRespondWith401ToGetExperienceIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/experience")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith0ExpIfNoExpEntriesForUser() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/experience")
        .then()
                .statusCode(OK.value())
                .body("experience", equalTo(0));
    }

    @Test
    void shouldRespondWithExp() {
        addExpEntry(10);
        addExpEntry(5);
        addExpEntry(15);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/experience")
        .then()
                .statusCode(OK.value())
                .body("experience", equalTo(30));
    }

    private void addExpEntry(int experience) {
        ExpEntry entry = new ExpEntry();
        entry.setUserId(userId);
        entry.setDate(LocalDateTime.now());
        entry.setExperience(experience);
        expRepository.save(entry);
    }
}