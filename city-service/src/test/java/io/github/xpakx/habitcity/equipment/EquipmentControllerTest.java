package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.config.SchedulerConfig;
import io.github.xpakx.habitcity.money.MoneyRepository;
import io.github.xpakx.habitcity.resource.Resource;
import io.github.xpakx.habitcity.resource.ResourceRepository;
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
}