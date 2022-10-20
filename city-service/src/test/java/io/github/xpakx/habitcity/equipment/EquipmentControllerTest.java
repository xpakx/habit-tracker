package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.config.SchedulerConfig;
import io.github.xpakx.habitcity.money.MoneyRepository;
import io.github.xpakx.habitcity.resource.ResourceRepository;
import io.github.xpakx.habitcity.shop.ShopEntryRepository;
import io.github.xpakx.habitcity.shop.ShopRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EquipmentControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;

    private final static int FULL_STACK = 64;

    @Autowired
    ResourceRepository resourceRepository;
    @Autowired
    UserEquipmentRepository equipmentRepository;
    @Autowired
    EquipmentEntryRepository entryRepository;
    @Autowired
    MoneyRepository moneyRepository;
    @MockBean
    SchedulerConfig config;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        entryRepository.deleteAll();
        resourceRepository.deleteAll();
        moneyRepository.deleteAll();
        equipmentRepository.deleteAll();
    }

    private Header getHeaderForUserId(Long userId) {
        return new Header("id", userId.toString());
    }

    @Test
    void shouldRespondWith401ToGetEquipmentIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/equipment")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToGetEquipmentIfEquipmentDoesNotExist() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/equipment")
        .then()
                .statusCode(NOT_FOUND.value());
    }
}