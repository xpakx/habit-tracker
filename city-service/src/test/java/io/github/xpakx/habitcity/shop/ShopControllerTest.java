package io.github.xpakx.habitcity.shop;

import io.github.xpakx.habitcity.config.SchedulerConfig;
import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.when;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShopControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;

    @Autowired
    ShopRepository shopRepository;
    @Autowired
    ShopEntryRepository entryRepository;
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
        shopRepository.deleteAll();
    }

    private Header getHeaderForUserId(Long userId) {
        return new Header("id", userId.toString());
    }

    @Test
    void shouldRespondWith401ToGetShopIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/shop/{shopId}", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

}