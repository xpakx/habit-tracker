package io.github.xpakx.habitcity.crafting;

import io.github.xpakx.habitcity.building.Building;
import io.github.xpakx.habitcity.building.BuildingRepository;
import io.github.xpakx.habitcity.city.City;
import io.github.xpakx.habitcity.city.CityBuilding;
import io.github.xpakx.habitcity.city.CityBuildingRepository;
import io.github.xpakx.habitcity.city.CityRepository;
import io.github.xpakx.habitcity.config.SchedulerConfig;
import io.github.xpakx.habitcity.crafting.dto.CraftRequest;
import io.github.xpakx.habitcity.crafting.dto.CraftRequestElem;
import io.github.xpakx.habitcity.equipment.EquipmentEntry;
import io.github.xpakx.habitcity.equipment.EquipmentEntryRepository;
import io.github.xpakx.habitcity.equipment.UserEquipment;
import io.github.xpakx.habitcity.equipment.UserEquipmentRepository;
import io.github.xpakx.habitcity.resource.Resource;
import io.github.xpakx.habitcity.resource.ResourceRepository;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CraftControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;

    private final static int FULL_STACK = 64;


    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    UserEquipmentRepository equipmentRepository;
    @Autowired
    EquipmentEntryRepository entryRepository;
    @Autowired
    ResourceRepository resourceRepository;
    @Autowired
    CityBuildingRepository cityBuildingRepository;
    @Autowired
    BuildingRepository buildingRepository;
    @Autowired
    CityRepository cityRepository;
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
        recipeRepository.deleteAll();
        resourceRepository.deleteAll();
        cityBuildingRepository.deleteAll();
        cityRepository.deleteAll();
        buildingRepository.deleteAll();
        equipmentRepository.deleteAll();
    }

    private Header getHeaderForUserId(Long userId) {
        return new Header("id", userId.toString());
    }

    @Test
    void shouldRespondWith401ToCraftIfNoUserIdGiven() {
        when()
                .post(baseUrl + "/craft")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith400ToCraftIfRecipeIsWrong() {
        CraftRequest request = createCraftRequest(List.of(addItem("item1")));
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/craft")
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private Long addItem(String name) {
        Resource resource = new Resource();
        resource.setName(name);
        resource.setCode(name.toUpperCase());
        resource.setRarity(0);
        resource.setBaseCost(0);
        resource.setMaxStock(64);
        return resourceRepository.save(resource).getId();
    }

    private CraftRequest createCraftRequest(List<Long> items) {
        CraftRequest request = new CraftRequest();
        request.setElem1(items.size()>0 ? toCraftElem(items.get(0)) : new CraftRequestElem());
        request.setElem2(items.size()>1 ? toCraftElem(items.get(1)) : new CraftRequestElem());
        request.setElem3(items.size()>2 ? toCraftElem(items.get(2)) : new CraftRequestElem());
        request.setElem4(items.size()>3 ? toCraftElem(items.get(3)) : new CraftRequestElem());
        request.setElem5(items.size()>4 ? toCraftElem(items.get(4)) : new CraftRequestElem());
        request.setElem6(items.size()>5 ? toCraftElem(items.get(5)) : new CraftRequestElem());
        request.setElem7(items.size()>6 ? toCraftElem(items.get(6)) : new CraftRequestElem());
        request.setElem8(items.size()>7 ? toCraftElem(items.get(7)) : new CraftRequestElem());
        request.setElem9(items.size()>8 ? toCraftElem(items.get(8)) : new CraftRequestElem());
        request.setAmount(1);
        return request;
    }

    private CraftRequestElem toCraftElem(Long id) {
        CraftRequestElem elem = new CraftRequestElem();
        elem.setId(id);
        return elem;
    }

    @Test
    void shouldRespondWith404ToCraftIfNotEquipmentFound() {
        List<Long> recipe = List.of(addItem("item1"));
        CraftRequest request = createCraftRequest(recipe);
        addRecipe(recipe, addItem("product"), null);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/craft")
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldRespondWith400ToCraftIfRequiredBuildingIsNotBuilt() {
        List<Long> recipe = List.of(addItem("item1"));
        createEquipment();
        CraftRequest request = createCraftRequest(recipe);
        addRecipe(recipe, addItem("product"), addBuilding("building1"));
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/craft")
        .then()
                .statusCode(BAD_REQUEST.value());
    }


    private void createEquipment() {
        UserEquipment equipment = new UserEquipment();
        equipment.setMaxSize(100);
        equipment.setUserId(userId);
        equipmentRepository.save(equipment);
    }

    private void addRecipe(List<Long> items, Long item, Long building) {
        Recipe recipe = new Recipe();
        recipe.setRequiredBuilding(building!=null ? buildingRepository.getReferenceById(building): null);
        recipe.setResource1(items.size()>0 ? resourceRepository.getReferenceById(items.get(0)) : null);
        recipe.setResource2(items.size()>1 ? resourceRepository.getReferenceById(items.get(1)) : null);
        recipe.setResource3(items.size()>2 ? resourceRepository.getReferenceById(items.get(2)) : null);
        recipe.setResource4(items.size()>3 ? resourceRepository.getReferenceById(items.get(3)) : null);
        recipe.setResource5(items.size()>4 ? resourceRepository.getReferenceById(items.get(4)) : null);
        recipe.setResource6(items.size()>5 ? resourceRepository.getReferenceById(items.get(5)) : null);
        recipe.setResource7(items.size()>6 ? resourceRepository.getReferenceById(items.get(6)) : null);
        recipe.setResource8(items.size()>7 ? resourceRepository.getReferenceById(items.get(7)) : null);
        recipe.setResource9(items.size()>8 ? resourceRepository.getReferenceById(items.get(8)) : null);
        recipe.setResource(item!=null ? resourceRepository.getReferenceById(item) : null);
        recipeRepository.save(recipe);
    }

    private Long addBuilding(String name) {
        Building building = new Building();
        building.setName(name);
        building.setBaseCost(0);
        building.setRarity(0);
        return buildingRepository.save(building).getId();
    }

    @Test
    void shouldRespondWith400ToCraftIfZeroResources() {
        List<Long> recipe = List.of(addItem("item1"));
        CraftRequest request = createCraftRequest(recipe);
        createEquipment();
        addRecipe(recipe, addItem("product"), null);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/craft")
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldRespondWith400ToCraftIfNotEnoughResources() {
        Long itemId = addItem("item1");
        List<Long> recipe = List.of(itemId, itemId, itemId, itemId);
        CraftRequest request = createCraftRequest(recipe);
        createEquipment();
        addResourceToEquipment(recipe.get(0), 3);
        addRecipe(recipe, addItem("product"), null);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/craft")
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private void addResourceToEquipment(Long resourceId, Integer amount) {
        EquipmentEntry entry = new EquipmentEntry();
        entry.setResource(resourceRepository.getReferenceById(resourceId));
        entry.setAmount(amount);
        entry.setEquipment(equipmentRepository.getByUserId(userId).get());
        entryRepository.save(entry);
    }

    @Test
    void shouldCraftItem() {
        Long itemId = addItem("item1");
        List<Long> recipe = List.of(itemId, itemId, itemId, itemId);
        CraftRequest request = createCraftRequest(recipe);
        createEquipment();
        addResourceToEquipment(recipe.get(0), 4);
        addRecipe(recipe, addItem("product"), null);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/craft")
        .then()
                .statusCode(OK.value());
    }

    @Test
    void shouldCraftItemWithBuilding() {
        Long itemId = addItem("item1");
        List<Long> recipe = List.of(itemId, itemId, itemId, itemId);
        CraftRequest request = createCraftRequest(recipe);
        createEquipment();
        addResourceToEquipment(recipe.get(0), 4);
        Long buildingId =  addBuilding("building1");
        addRecipe(recipe, addItem("product"), buildingId);
        buildBuilding(buildingId, createCity());
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/craft")
        .then()
                .statusCode(OK.value());
    }

    private void buildBuilding(Long buildingId, Long cityId) {
        CityBuilding building = new CityBuilding();
        building.setBuilding(buildingRepository.getReferenceById(buildingId));
        building.setCity(cityRepository.getReferenceById(cityId));
        cityBuildingRepository.save(building);
    }

    private Long createCity() {
        City city = new City();
        city.setUserId(userId);
        city.setMaxSize(50);
        return cityRepository.save(city).getId();
    }

    @Test
    void shouldSubtractResources() {
        Long itemId = addItem("item1");
        List<Long> recipe = List.of(itemId, itemId, itemId, itemId);
        CraftRequest request = createCraftRequest(recipe);
        createEquipment();
        addResourceToEquipment(recipe.get(0), 5);
        addRecipe(recipe, addItem("product"), null);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/craft")
        .then()
                .statusCode(OK.value());
        List<EquipmentEntry> entries = entryRepository.findAll();
        assertThat(entries, hasSize(2));
        assertThat(entries, hasItem(
                both(hasProperty("amount", equalTo(1))).and(hasProperty("resource", hasProperty("name", equalTo("item1"))))
        ));
    }

    @Test
    void shouldDeleteEmptyStacks() {
        Long itemId = addItem("item1");
        List<Long> recipe = List.of(itemId, itemId, itemId, itemId);
        CraftRequest request = createCraftRequest(recipe);
        createEquipment();
        addResourceToEquipment(recipe.get(0), 4);
        addRecipe(recipe, addItem("product"), null);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/craft")
        .then()
                .statusCode(OK.value());
        List<EquipmentEntry> entries = entryRepository.findAll();
        assertThat(entries, not(hasItem(
                hasProperty("name", equalTo("item1")))
        ));
    }

    @Test
    void shouldSubtractMultipleResources() {
        Long item1Id = addItem("item1");
        Long item2Id = addItem("item2");
        Long item3Id = addItem("item3");
        List<Long> recipe = List.of(item1Id, item1Id, item2Id, item3Id);
        CraftRequest request = createCraftRequest(recipe);
        createEquipment();
        addResourceToEquipment(item1Id, 5);
        addResourceToEquipment(item2Id, 15);
        addResourceToEquipment(item3Id, 10);
        addRecipe(recipe, addItem("product"), null);
        given()
                .header(getHeaderForUserId(userId))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/craft")
        .then()
                .statusCode(OK.value());
        List<EquipmentEntry> entries = entryRepository.findAll();
        assertThat(entries, hasItem(
                both(hasProperty("amount", equalTo(3))).and(hasProperty("resource", hasProperty("name", equalTo("item1"))))
        ));
        assertThat(entries, hasItem(
                both(hasProperty("amount", equalTo(14))).and(hasProperty("resource", hasProperty("name", equalTo("item2"))))
        ));
        assertThat(entries, hasItem(
                both(hasProperty("amount", equalTo(9))).and(hasProperty("resource", hasProperty("name", equalTo("item3"))))
        ));
    }
}