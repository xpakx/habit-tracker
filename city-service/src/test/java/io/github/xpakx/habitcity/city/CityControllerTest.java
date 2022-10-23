package io.github.xpakx.habitcity.city;

import io.github.xpakx.habitcity.building.Building;
import io.github.xpakx.habitcity.building.BuildingRepository;
import io.github.xpakx.habitcity.city.dto.BuildingRequest;
import io.github.xpakx.habitcity.config.SchedulerConfig;
import io.github.xpakx.habitcity.equipment.UserEquipment;
import io.github.xpakx.habitcity.equipment.UserEquipmentRepository;
import io.github.xpakx.habitcity.money.Money;
import io.restassured.http.ContentType;
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
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CityControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;

    private final static int FULL_STACK = 64;

    @Autowired
    CityRepository cityRepository;
    @Autowired
    BuildingRepository buildingRepository;
    @Autowired
    CityBuildingRepository cityBuildingRepository;
    @Autowired
    UserEquipmentRepository equipmentRepository;
    @MockBean
    SchedulerConfig config;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        equipmentRepository.deleteAll();
        cityBuildingRepository.deleteAll();
        buildingRepository.deleteAll();
        cityRepository.deleteAll();
    }

    private Header getHeaderForUserId(Long userId) {
        return new Header("id", userId.toString());
    }

    @Test
    void shouldRespondWith401ToGetCitiesIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/city/all")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWithEmptyCityList() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/city/all")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(0));
    }

    @Test
    void shouldRespondWithCityListWithOneElem() {
        Long cityId = createCity();
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/city/all")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(1))
                .body("id", hasItem(cityId.intValue()));
    }

    @Test
    void shouldRespondWithCityList() {
        Long city1Id = createCity();
        Long city2Id = createCity();
        Long city3Id = createCity();
        Long city4Id = createCity(userId+1);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/city/all")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(3))
                .body("id", hasItems(city1Id.intValue(), city2Id.intValue(), city3Id.intValue()))
                .body("id", not(hasItem(city4Id.intValue())));
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
    void shouldRespondWith401ToGetBuildingsIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/city/{cityId}/building", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToGetBuildingsIfCityNotFound() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/city/{cityId}/building", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldRespondWithEmptyListToGetBuildings() {
        Long cityId = createCity();
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/city/{cityId}/building", cityId)
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(0));
    }

    @Test
    void shouldRespondWith404ToGetBuildingsInCityBelongingToDifferentUser() {
        Long cityId = createCity(userId+1);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/city/{cityId}/building", cityId)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldRespondWithBuildingList() {
        Long cityId = createCity();
        Long city2Id = createCity();
        addBuilding("building1", cityId);
        addBuilding("building2", cityId);
        addBuilding("building3", city2Id);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/city/{cityId}/building", cityId)
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(2))
                .body("name", hasItem("building1"))
                .body("name", hasItem("building2"))
                .body("name", not(hasItem("building3")));
    }

    private void addBuilding(String buildingName, Long cityId) {
        Building building = new Building();
        building.setName(buildingName);
        Long buildingId = buildingRepository.save(building).getId();
        CityBuilding cityBuilding = new CityBuilding();
        cityBuilding.setCity(cityRepository.getReferenceById(cityId));
        cityBuilding.setBuilding(buildingRepository.getReferenceById(buildingId));
        cityBuildingRepository.save(cityBuilding);
    }

    @Test
    void shouldRespondWith401ToBuildIfNoUserIdGiven() {
        when()
                .post(baseUrl + "/city/{cityId}/building", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToBuildIfCityNotFound() {
        BuildingRequest request = getBuildingRequest(1L);
        createEquipment();
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/building", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private BuildingRequest getBuildingRequest(Long id) {
        BuildingRequest request = new BuildingRequest();
        request.setBuildingId(id);
        return request;
    }

    private void createEquipment() {
        UserEquipment equipment = new UserEquipment();
        equipment.setMaxSize(100);
        equipment.setUserId(userId);
        equipmentRepository.save(equipment);
    }
}