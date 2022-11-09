package io.github.xpakx.habitcity.ship;

import io.github.xpakx.habitcity.city.City;
import io.github.xpakx.habitcity.city.CityRepository;
import io.github.xpakx.habitcity.clients.ExpeditionPublisher;
import io.github.xpakx.habitcity.config.SchedulerConfig;
import io.github.xpakx.habitcity.equipment.EquipmentEntry;
import io.github.xpakx.habitcity.equipment.EquipmentEntryRepository;
import io.github.xpakx.habitcity.equipment.UserEquipment;
import io.github.xpakx.habitcity.equipment.UserEquipmentRepository;
import io.github.xpakx.habitcity.resource.Resource;
import io.github.xpakx.habitcity.resource.ResourceRepository;
import io.github.xpakx.habitcity.ship.dto.ExpeditionEquipment;
import io.github.xpakx.habitcity.ship.dto.ExpeditionRequest;
import io.github.xpakx.habitcity.ship.dto.ExpeditionShip;
import io.github.xpakx.habitcity.ship.dto.ShipRequest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;

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
    @Autowired
    ResourceRepository resourceRepository;
    @MockBean
    SchedulerConfig config;
    @MockBean
    ExpeditionPublisher publisher;

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
        resourceRepository.deleteAll();
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
                .body("$", hasSize(1))
                .body("name", hasItem("ship1"));
    }

    private Long deployShip(Long cityId, Long shipId) {
        return deployShip(cityId, shipId, false);
    }

    private Long deployShip(Long cityId, Long shipId, boolean blocked) {
        PlayerShip ship = new PlayerShip();
        ship.setBlocked(blocked);
        ship.setCity(cityRepository.getReferenceById(cityId));
        ship.setShip(shipRepository.getReferenceById(shipId));
        return playerShipRepository.save(ship).getId();
    }

    private Long createShip(String name) {
        return createShip(name, 10);
    }

    private Long createShip(String name, int maxCargo) {
        Ship ship = new Ship();
        ship.setName(name);
        ship.setMaxCargo(maxCargo);
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
                .body("$", hasSize(2))
                .body("name", hasItem("ship1"))
                .body("name", hasItem("ship2"))
                .body("name", not(hasItem("ship3")));
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

    @Test
    void shouldRespondWith401ToDeployShipIfNoUserIdGiven() {
        when()
                .post(baseUrl + "/city/{cityId}/ship", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotDeployShipIfPlayerDoesNotOwnCity() {
        createEquipment();
        Long entryId = addShipToEquipment(createShip("ship1"));
        Long cityId = createCity(userId+1);
        ShipRequest request = createShipRequest(entryId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/ship", cityId)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private ShipRequest createShipRequest(Long entryId) {
        ShipRequest request = new ShipRequest();
        request.setEntryId(entryId);
        return request;
    }

    private Long addShipToEquipment(Long shipId) {
        EquipmentEntry entry = new EquipmentEntry();
        entry.setShip(shipRepository.getReferenceById(shipId));
        entry.setAmount(1);
        entry.setEquipment(equipmentRepository.getByUserId(userId).get());
        return entryRepository.save(entry).getId();
    }

    private Long addResourceToEquipment(Long resourceId) {
        return addResourceToEquipment(resourceId, 1);
    }

    private Long addResourceToEquipment(Long resourceId, int amount) {
        EquipmentEntry entry = new EquipmentEntry();
        entry.setResource(resourceRepository.getReferenceById(resourceId));
        entry.setAmount(amount);
        entry.setEquipment(equipmentRepository.getByUserId(userId).get());
        return entryRepository.save(entry).getId();
    }

    private void createEquipment() {
        UserEquipment equipment = new UserEquipment();
        equipment.setMaxSize(100);
        equipment.setUserId(userId);
        equipmentRepository.save(equipment);
    }

    @Test
    void shouldNotDeployShipIfCityDoesNotExist() {
        createEquipment();
        Long entryId = addShipToEquipment(createShip("ship1"));
        ShipRequest request = createShipRequest(entryId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/ship", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldNotDeployShipIfEntryDoesNotExist() {
        createEquipment();
        Long cityId = createCity(userId+1);
        createShip("ship1");
        ShipRequest request = createShipRequest(1L);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/ship", cityId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotDeployShipIfEntryDoesNotHaveShip() {
        createEquipment();
        Long cityId = createCity(userId+1);
        Long entryId = addResourceToEquipment(createResource("item1"));
        ShipRequest request = createShipRequest(entryId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/ship", cityId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private Long createResource(String itemName) {
        Resource res = new Resource();
        res.setName(itemName);
        res.setBaseCost(5);
        res.setCode(itemName.toUpperCase());
        res.setMaxStock(FULL_STACK);
        res.setRarity(0);
        return resourceRepository.save(res).getId();
    }

    @Test
    void shouldDeployShip() {
        createEquipment();
        Long entryId = addShipToEquipment(createShip("ship1"));
        Long cityId = createCity(userId);
        ShipRequest request = createShipRequest(entryId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/ship", cityId)
        .then()
                .statusCode(OK.value());
    }

    @Test
    void shouldDeleteEquipmentEntry() {
        createEquipment();
        Long entryId = addShipToEquipment(createShip("ship1"));
        Long cityId = createCity(userId);
        ShipRequest request = createShipRequest(entryId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/ship", cityId)
        .then()
                .statusCode(OK.value());
        Optional<EquipmentEntry> entry = entryRepository.findById(entryId);
        assertThat(entry.isEmpty(), is(true));
    }

    @Test
    void shouldAddShipEntry() {
        createEquipment();
        Long entryId = addShipToEquipment(createShip("ship1"));
        Long cityId = createCity(userId);
        ShipRequest request = createShipRequest(entryId);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/ship", cityId)
        .then()
                .statusCode(OK.value());
        List<PlayerShip> ships = playerShipRepository.findAll();
        assertThat(ships.size(), equalTo(1));
        assertThat(ships.get(0).getShip().getName(), is("ship1"));
    }

    @Test
    void shouldRespondWith401ToSendExpeditionIfNoUserIdGiven() {
        when()
                .post(baseUrl + "/city/{cityId}/expedition", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotSendExpeditionFromNonexistentCity() {
        ExpeditionRequest request = getExpeditionRequest();
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/expedition", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private ExpeditionRequest getExpeditionRequest() {
        return getExpeditionRequest(new ArrayList<>());
    }

    private ExpeditionRequest getExpeditionRequest(List<ExpeditionShip> ships) {
        ExpeditionRequest request = new ExpeditionRequest();
        request.setIslandId(1L);
        request.setShips(ships);
        return request;
    }

    @Test
    void shouldNotSendExpeditionWithEmptyShipList() {
        ExpeditionRequest request = getExpeditionRequest();
        Long cityId = createCity();
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/expedition", cityId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotSendExpeditionWithNullShipList() {
        ExpeditionRequest request = getExpeditionRequest(null);
        Long cityId = createCity();
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/expedition", cityId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotSendExpeditionWithShipFromWrongCity() {
        Long cityId = createCity();
        Long otherCityId = createCity();
        Long shipId = createShip("ship1");
        ExpeditionRequest request = getExpeditionRequest(getShipList(List.of(deployShip(cityId, shipId), deployShip(cityId, shipId), deployShip(otherCityId, shipId))));
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/expedition", cityId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private List<ExpeditionShip> getShipList(List<Long> ids) {
        List<ExpeditionShip> list = new ArrayList<>();
        for(Long id : ids) {
            ExpeditionShip ship = new ExpeditionShip();
            ship.setShipId(id);
            ship.setEquipment(new ArrayList<>());
            list.add(ship);
        }
        return list;
    }

    @Test
    void shouldNotSendExpeditionWithAlreadyBlockedShip() {
        Long cityId = createCity();
        Long shipId = createShip("ship1");
        ExpeditionRequest request = getExpeditionRequest(getShipList(List.of(deployShip(cityId, shipId, true))));
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/expedition", cityId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldSendExpeditionWithoutCargo() {
        Long cityId = createCity();
        createEquipment();
        Long shipId = createShip("ship1");
        ExpeditionRequest request = getExpeditionRequest(getShipList(List.of(deployShip(cityId, shipId))));
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/expedition", cityId)
        .then()
                .statusCode(OK.value());
    }

    @Test
    void shouldBlockShip() {
        Long cityId = createCity();
        createEquipment();
        Long shipId = createShip("ship1");
        ExpeditionRequest request = getExpeditionRequest(getShipList(List.of(deployShip(cityId, shipId))));
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/expedition", cityId)
        .then()
                .statusCode(OK.value());
        List<PlayerShip> ships = playerShipRepository.findAll();
        assertThat(ships.size(), equalTo(1));
        assertThat(ships, everyItem(hasProperty("blocked", equalTo(true))));
    }

    @Test
    void shouldNotSendExpeditionWithOverloadedCargo() {
        Long cityId = createCity();
        createEquipment();
        Long resourceId = createResource("item1");
        addResourceToEquipment(resourceId, 11);
        Long shipId = createShip("ship1");
        ExpeditionRequest request = getExpeditionRequest(getShipList(List.of(deployShip(cityId, shipId))));
        request.getShips().get(0).getEquipment().add(getCargo(resourceId, 11));
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/expedition", cityId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private ExpeditionEquipment getCargo(Long resourceId, int amount) {
        ExpeditionEquipment request = new ExpeditionEquipment();
        request.setAmount(amount);
        request.setId(resourceId);
        return request;
    }

    @Test
    void shouldNotSendExpeditionWithItemsPlayerDoNotHave() {
        Long cityId = createCity();
        createEquipment();
        Long resourceId = createResource("item1");
        Long shipId = createShip("ship1");
        ExpeditionRequest request = getExpeditionRequest(getShipList(List.of(deployShip(cityId, shipId))));
        request.getShips().get(0).getEquipment().add(getCargo(resourceId, 10));
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(baseUrl + "/city/{cityId}/expedition", cityId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldNotSendExpeditionWithTooMuchItems() {
        Long cityId = createCity();
        createEquipment();
        Long resourceId = createResource("item1");
        addResourceToEquipment(resourceId, 9);
        Long shipId = createShip("ship1");
        ExpeditionRequest request = getExpeditionRequest(getShipList(List.of(deployShip(cityId, shipId))));
        request.getShips().get(0).getEquipment().add(getCargo(resourceId, 10));
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/expedition", cityId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldSendExpeditionWithCargo() {
        Long cityId = createCity();
        createEquipment();
        Long resourceId = createResource("item1");
        addResourceToEquipment(resourceId, 10);
        Long shipId = createShip("ship1");
        ExpeditionRequest request = getExpeditionRequest(getShipList(List.of(deployShip(cityId, shipId))));
        request.getShips().get(0).getEquipment().add(getCargo(resourceId, 10));
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/expedition", cityId)
        .then()
                .statusCode(OK.value());
    }

    @Test
    void shouldSubtractResources() {
        Long cityId = createCity();
        createEquipment();
        Long resourceId = createResource("item1");
        addResourceToEquipment(resourceId, 10);
        Long shipId = createShip("ship1");
        ExpeditionRequest request = getExpeditionRequest(getShipList(List.of(deployShip(cityId, shipId))));
        request.getShips().get(0).getEquipment().add(getCargo(resourceId, 10));
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/expedition", cityId)
        .then()
                .statusCode(OK.value());
        List<EquipmentEntry> entries = entryRepository.findAll();
        assertThat(entries, hasSize(0));
    }

    @Test
    void shouldSendExpeditionWithMultipleShipsAndCargo() {
        Long cityId = createCity();
        createEquipment();
        Long resourceId = createResource("item1");
        Long resource2Id = createResource("item2");
        Long resource3Id = createResource("item3");
        addResourceToEquipment(resourceId, 64);
        addResourceToEquipment(resourceId, 64);
        addResourceToEquipment(resource2Id, 21);
        addResourceToEquipment(resource3Id, 30);
        Long shipId = createShip("ship1", 100);
        Long ship2Id = createShip("ship2", 120);
        ExpeditionRequest request = getExpeditionRequest(getShipList(List.of(deployShip(cityId, shipId), deployShip(cityId, shipId), deployShip(cityId, ship2Id))));
        request.getShips().get(0).getEquipment().add(getCargo(resourceId, 20));
        request.getShips().get(0).getEquipment().add(getCargo(resource2Id, 10));
        request.getShips().get(1).getEquipment().add(getCargo(resourceId, 50));
        request.getShips().get(1).getEquipment().add(getCargo(resource2Id, 10));
        request.getShips().get(1).getEquipment().add(getCargo(resource3Id, 10));
        request.getShips().get(2).getEquipment().add(getCargo(resourceId, 40));
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/expedition", cityId)
        .then()
                .statusCode(OK.value());
        List<EquipmentEntry> entries = entryRepository.findAll();
        assertThat(entries, hasSize(3));
        assertThat(entries, hasItem(
                both(hasProperty("resource", hasProperty("name", equalTo("item1"))))
                        .and(hasProperty("amount", equalTo(18)))
        ));
        assertThat(entries, hasItem(
                both(hasProperty("resource", hasProperty("name", equalTo("item2"))))
                        .and(hasProperty("amount", equalTo(1)))
        ));
        assertThat(entries, hasItem(
                both(hasProperty("resource", hasProperty("name", equalTo("item3"))))
                        .and(hasProperty("amount", equalTo(20)))
        ));
    }

    @Test
    void shouldBlockAllShips() {
        Long cityId = createCity();
        createEquipment();
        Long shipId = createShip("ship1", 100);
        Long ship2Id = createShip("ship2", 120);
        ExpeditionRequest request = getExpeditionRequest(getShipList(List.of(deployShip(cityId, shipId), deployShip(cityId, shipId), deployShip(cityId, ship2Id))));
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/expedition", cityId)
        .then()
                .statusCode(OK.value());
        List<PlayerShip> entries = playerShipRepository.findAll();
        assertThat(entries, everyItem(hasProperty("blocked", equalTo(true))));
    }

    @Test
    void shouldNotSendShipsIfTooMuchCumulativeCargo() {
        Long cityId = createCity();
        createEquipment();
        Long resourceId = createResource("item1");
        addResourceToEquipment(resourceId, 64);
        Long shipId = createShip("ship1", 100);
        Long ship2Id = createShip("ship2", 120);
        ExpeditionRequest request = getExpeditionRequest(getShipList(List.of(deployShip(cityId, shipId), deployShip(cityId, shipId), deployShip(cityId, ship2Id))));
        request.getShips().get(0).getEquipment().add(getCargo(resourceId, 20));
        request.getShips().get(1).getEquipment().add(getCargo(resourceId, 50));
        request.getShips().get(2).getEquipment().add(getCargo(resourceId, 40));
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/city/{cityId}/expedition", cityId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }
}