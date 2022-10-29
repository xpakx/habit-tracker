package io.github.xpakx.habitcity.ship;

import io.github.xpakx.habitcity.city.City;
import io.github.xpakx.habitcity.city.CityRepository;
import io.github.xpakx.habitcity.config.SchedulerConfig;
import io.github.xpakx.habitcity.equipment.EquipmentEntryRepository;
import io.github.xpakx.habitcity.equipment.UserEquipmentRepository;
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
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShipControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;

    private final static int FULL_STACK = 64;

    @Autowired
    CityRepository cityRepository;
    @Autowired
    UserEquipmentRepository equipmentRepository;
    @Autowired
    EquipmentEntryRepository entryRepository;
    @Autowired
    ShipRepository shipRepository;
    @Autowired
    PlayerShipRepository playerShipRepository;
    @MockBean
    SchedulerConfig config;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        playerShipRepository.deleteAll();
        entryRepository.deleteAll();
        shipRepository.deleteAll();
        equipmentRepository.deleteAll();
        cityRepository.deleteAll();
    }

    private Header getHeaderForUserId(Long userId) {
        return new Header("id", userId.toString());
    }


    @Test
    void shouldRespondWith401ToGetShipsIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/city/{cityId}/ship/all", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWithEmptyListIfNoCityExist() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/city/{cityId}/ship/all", 1L)
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(0));
    }

    @Test
    void shouldRespondWithEmptyListIfNoShipsDeployedToCity() {
        Long cityId = createCity();
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/city/{cityId}/ship/all", cityId)
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(0));
    }

    private Long createCity() {
        return createCity(userId);
    }

    private Long createCity(Long userId) {
        City city = new City();
        city.setMaxSize(10);
        city.setUserId(userId);
        return cityRepository.save(city).getId();
    }

    @Test
    void shouldRespondWithShipsDeployedToCity() {
        Long cityId = createCity();
        deployShip(cityId, createShip("ship1"));
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/city/{cityId}/ship/all", cityId)
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(1));
    }

    private void deployShip(Long cityId, Long shipId) {
        PlayerShip ship = new PlayerShip();
        ship.setBlocked(false);
        ship.setCity(cityRepository.getReferenceById(cityId));
        ship.setShip(shipRepository.getReferenceById(shipId));
        playerShipRepository.save(ship);
    }

    private Long createShip(String name) {
        Ship ship = new Ship();
        ship.setName(name);
        return shipRepository.save(ship).getId();
    }

    @Test
    void shouldNotIncludeShipsInOtherCitiesInResponse() {
        Long cityId = createCity();
        Long city2Id = createCity();
        deployShip(cityId, createShip("ship1"));
        deployShip(cityId, createShip("ship2"));
        deployShip(city2Id, createShip("ship3"));
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/city/{cityId}/ship/all", cityId)
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(2));
    }

    @Test
    void shouldRespondWithEmptyListIfCityDoesNotBelongToUser() {
        Long cityId = createCity(userId+1);
        deployShip(cityId, createShip("ship1"));
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/city/{cityId}/ship/all", cityId)
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(0));
    }

}