package io.github.xpakx.habitcity.shop;

import io.github.xpakx.habitcity.config.SchedulerConfig;
import io.github.xpakx.habitcity.equipment.EquipmentEntry;
import io.github.xpakx.habitcity.equipment.EquipmentEntryRepository;
import io.github.xpakx.habitcity.equipment.UserEquipment;
import io.github.xpakx.habitcity.equipment.UserEquipmentRepository;
import io.github.xpakx.habitcity.money.Money;
import io.github.xpakx.habitcity.money.MoneyRepository;
import io.github.xpakx.habitcity.resource.Resource;
import io.github.xpakx.habitcity.resource.ResourceRepository;
import io.github.xpakx.habitcity.shop.dto.BuyRequest;
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
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShopControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;

    private final static int FULL_STACK = 64;

    @Autowired
    ShopRepository shopRepository;
    @Autowired
    ShopEntryRepository entryRepository;
    @Autowired
    ResourceRepository resourceRepository;
    @Autowired
    UserEquipmentRepository equipmentRepository;
    @Autowired
    EquipmentEntryRepository eqEntryRepository;
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
        eqEntryRepository.deleteAll();
        resourceRepository.deleteAll();
        shopRepository.deleteAll();
        moneyRepository.deleteAll();
        equipmentRepository.deleteAll();
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

    @Test
    void shouldRespondWith404IfShopDoesNotExist() {
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/shop/{shopId}", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldRespondWith404IfShopForActiveUserDoesNotExist() {
        Long shopId = createShop(userId+1);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/shop/{shopId}", shopId)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private Long createShop() {
        return createShop(userId);
    }

    private Long createShop(Long ownerId) {
        Shop shop = new Shop();
        shop.setMaxRarity(1);
        shop.setMaxSize(5);
        shop.setUserId(ownerId);
        return shopRepository.save(shop).getId();
    }

    @Test
    void shouldRespondWithEmptyShop() {
        Long shopId = createShop();
        given()
                .header(getHeaderForUserId(userId))
       .when()
                .get(baseUrl + "/shop/{shopId}", shopId)
       .then()
                .statusCode(OK.value())
                .body("items", hasSize(0));
    }


    @Test
    void shouldRespondWithShop() {
        Long shopId = createShop();
        addItemToShop("item1", 50, 10, shopId);
        addItemToShop("item2", 10, 11, shopId);
        addItemToShop("item3", 25, 5, shopId);
        given()
                .header(getHeaderForUserId(userId))
        .when()
                .get(baseUrl + "/shop/{shopId}", shopId)
        .then()
                .statusCode(OK.value())
                .body("items", hasSize(3))
                .body("items.name", hasItem("item1"))
                .body("items.name", hasItem("item2"))
                .body("items.name", hasItem("item3"));
    }

    private Long addItemToShop(String itemName, int amount, int price, Long shopId) {
        Resource res = new Resource();
        res.setName(itemName);
        res.setBaseCost(price);
        res.setCode(itemName.toUpperCase());
        res.setMaxStock(FULL_STACK);
        res.setRarity(0);
        res = resourceRepository.save(res);
        ShopEntry entry = new ShopEntry();
        entry.setAmount(amount);
        entry.setPrice(price);
        entry.setResource(res);
        entry.setShop(shopRepository.getReferenceById(shopId));
        return entryRepository.save(entry).getId();
    }

    @Test
    void shouldRespondWith401ToBuyIfNoUserIdGiven() {
        when()
                .post(baseUrl + "/shop/item/{entryId}", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404IfEntryDoesNotExist() {
        BuyRequest request = getBuyRequest(1);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/shop/item/{entryId}", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private BuyRequest getBuyRequest(int amount) {
        BuyRequest request = new BuyRequest();
        request.setAmount(amount);
        return request;
    }

    @Test
    void shouldRespondWith404IfEntryBelongsToDifferentPlayer() {
        Long shopId = createShop(userId+1);
        Long entryId = addItemToShop("item1", 10, 20, shopId);
        BuyRequest request = getBuyRequest(1);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/shop/item/{entryId}", entryId)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldRespondWith400IfAmountIsTooLarge() {
        Long shopId = createShop(userId);
        Long entryId = addItemToShop("item1", 10, 20, shopId);
        BuyRequest request = getBuyRequest(100);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/shop/item/{entryId}", entryId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldRespondWith404IfUserEquipmentIsNotCreated() {
        Long shopId = createShop(userId);
        Long entryId = addItemToShop("item1", 10, 20, shopId);
        createEquipment(0);
        BuyRequest request = getBuyRequest(1);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/shop/item/{entryId}", entryId)
        .then()
                .statusCode(NOT_FOUND.value());
    }


    @Test
    void shouldRespondWith400IfEquipmentFull() {
        Long shopId = createShop(userId);
        Long entryId = addItemToShop("item1", 10, 20, shopId);
        createEquipment(0);
        BuyRequest request = getBuyRequest(1);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/shop/item/{entryId}", entryId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private void createEquipment(int capacity) {
        createEquipment(capacity, 0L);
    }

    private void createEquipment(int capacity, long cash) {
        UserEquipment equipment = new UserEquipment();
        equipment.setMaxSize(capacity);
        equipment.setUserId(userId);
        equipment = equipmentRepository.save(equipment);
        Money money = new Money();
        money.setAmount(cash);
        money.setEquipment(equipment);
        moneyRepository.save(money);
    }

    @Test
    void shouldRespondWith400IfEquipmentHasOnlyFullStackOfGivenItem() {
        Long shopId = createShop(userId);
        Long entryId = addItemToShop("item1", 10, 20, shopId);
        createEquipment(1);
        addItemToEquipment(entryId, FULL_STACK);
        BuyRequest request = getBuyRequest(1);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/shop/item/{entryId}", entryId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private void addItemToEquipment(Long entryId, int amount) {
        EquipmentEntry entry = new EquipmentEntry();
        entry.setAmount(amount);
        entry.setEquipment(equipmentRepository.getByUserId(userId).orElse(null));
        entry.setResource(entryRepository.findById(entryId).get().getResource());
        eqEntryRepository.save(entry);
    }


    @Test
    void shouldRespondWith400IfEquipmentHasOnlyNonFullStackOfOtherItem() {
        Long shopId = createShop(userId);
        Long entryId = addItemToShop("item1", 10, 20, shopId);
        Long entry2Id = addItemToShop("item2", 10, 20, shopId);
        createEquipment(1);
        addItemToEquipment(entry2Id, 10);
        BuyRequest request = getBuyRequest(1);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(baseUrl + "/shop/item/{entryId}", entryId)
                .then()
                .statusCode(BAD_REQUEST.value());
    }
}