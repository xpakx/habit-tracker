package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.building.Building;
import io.github.xpakx.habitcity.building.BuildingRepository;
import io.github.xpakx.habitcity.config.SchedulerConfig;
import io.github.xpakx.habitcity.money.MoneyRepository;
import io.github.xpakx.habitcity.resource.Resource;
import io.github.xpakx.habitcity.resource.ResourceRepository;
import io.github.xpakx.habitcity.ship.Ship;
import io.github.xpakx.habitcity.ship.ShipRepository;
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
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.OK;

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
    BuildingRepository buildingRepository;
    @Autowired
    ShipRepository shipRepository;
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
        buildingRepository.deleteAll();
        shipRepository.deleteAll();
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

    private void createEquipment() {
        UserEquipment equipment = new UserEquipment();
        equipment.setMaxSize(50);
        equipment.setUserId(userId);
        equipmentRepository.save(equipment);
    }

    private void addItemToEquipment(Long entryId, int amount) {
        EquipmentEntry entry = new EquipmentEntry();
        entry.setAmount(amount);
        entry.setEquipment(equipmentRepository.getByUserId(userId).orElse(null));
        entry.setResource(resourceRepository.getReferenceById(entryId));
        entryRepository.save(entry);
    }

    private Long addResource(String itemName) {
        Resource res = new Resource();
        res.setName(itemName);
        res.setBaseCost(5);
        res.setCode(itemName.toUpperCase());
        res.setMaxStock(FULL_STACK);
        res.setRarity(0);
        return resourceRepository.save(res).getId();
    }

    @Test
    void shouldRespondWithEmptyEquipment() {
        createEquipment();
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/equipment")
        .then()
                .statusCode(OK.value())
                .body("items", hasSize(0));
    }

    @Test
    void shouldReturnListOfItemsInEquipment() {
        createEquipment();
        Long item1 = addResource("item1");
        Long item2 = addResource("item2");
        Long item3 = addResource("item3");
        addItemToEquipment(item1, 40);
        addItemToEquipment(item1, 30);
        addItemToEquipment(item2, 64);
        addItemToEquipment(item3, 15);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/equipment")
        .then()
                .statusCode(OK.value())
                .body("items", hasSize(4));
    }

    private Long addShip(String itemName) {
        Ship res = new Ship();
        res.setName(itemName);
        res.setBaseCost(5);
        res.setCode(itemName.toUpperCase());
        res.setSize(3);
        res.setRarity(0);
        return shipRepository.save(res).getId();
    }

    private Long addBuilding(String itemName) {
        Building res = new Building();
        res.setName(itemName);
        res.setBaseCost(5);
        res.setCode(itemName.toUpperCase());
        res.setRarity(0);
        return buildingRepository.save(res).getId();
    }

    private void addShipToEquipment(Long shipId) {
        EquipmentEntry entry = new EquipmentEntry();
        entry.setAmount(1);
        entry.setEquipment(equipmentRepository.getByUserId(userId).orElse(null));
        entry.setShip(shipRepository.getReferenceById(shipId));
        entryRepository.save(entry);
    }

    private void addBuildingToEquipment(Long buildingId) {
        EquipmentEntry entry = new EquipmentEntry();
        entry.setAmount(1);
        entry.setEquipment(equipmentRepository.getByUserId(userId).orElse(null));
        entry.setBuilding(buildingRepository.getReferenceById(buildingId));
        entryRepository.save(entry);
    }

    @Test
    void shouldRespondWith401ToGetShipsIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/equipment/ship")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToGetShipsIfEquipmentDoesNotExist() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/equipment/ship")
        .then()
                .statusCode(NOT_FOUND.value());
    }
    @Test
    void shouldRespondWithEmptyShipList() {
        createEquipment();
        addItemToEquipment(addResource("item1"), 40);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/equipment/ship")
        .then()
                .statusCode(OK.value())
                .body("items", hasSize(0));
    }

    @Test
    void shouldReturnListOfShipsInEquipment() {
        createEquipment();
        addItemToEquipment(addResource("item1"), 40);
        addBuildingToEquipment(addBuilding("building1"));
        addShipToEquipment(addShip("ship1"));
        addShipToEquipment(addShip("ship2"));
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/equipment/ship")
        .then()
                .statusCode(OK.value())
                .body("items", hasSize(2))
                .body("items.name", hasItem("ship1"))
                .body("items.name", hasItem("ship2"))
                .body("items.name", not(hasItem("building1")))
                .body("items.name", not(hasItem("item1")));
    }

    @Test
    void shouldRespondWith401ToGetBuildingsIfNoUserIdGiven() {
        when()
                .get(baseUrl + "/equipment/building")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToGetBuildingsIfEquipmentDoesNotExist() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/equipment/building")
        .then()
                .statusCode(NOT_FOUND.value());
    }
    @Test
    void shouldRespondWithEmptyBuildingList() {
        createEquipment();
        addItemToEquipment(addResource("item1"), 40);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/equipment/building")
        .then()
                .statusCode(OK.value())
                .body("items", hasSize(0));
    }

    @Test
    void shouldReturnListOfBuildingsInEquipment() {
        createEquipment();
        addItemToEquipment(addResource("item1"), 40);
        addBuildingToEquipment(addBuilding("building1"));
        addBuildingToEquipment(addBuilding("building2"));
        addShipToEquipment(addShip("ship1"));
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/equipment/building")
        .then()
                .statusCode(OK.value())
                .body("items", hasSize(2))
                .body("items.name", hasItem("building1"))
                .body("items.name", hasItem("building2"))
                .body("items.name", not(hasItem("ship1")))
                .body("items.name", not(hasItem("item1")));
    }
}