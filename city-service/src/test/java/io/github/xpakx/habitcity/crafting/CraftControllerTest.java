package io.github.xpakx.habitcity.crafting;

import io.github.xpakx.habitcity.building.Building;
import io.github.xpakx.habitcity.building.BuildingRepository;
import io.github.xpakx.habitcity.city.CityBuildingRepository;
import io.github.xpakx.habitcity.city.CityService;
import io.github.xpakx.habitcity.config.SchedulerConfig;
import io.github.xpakx.habitcity.crafting.dto.CraftRequest;
import io.github.xpakx.habitcity.crafting.dto.CraftRequestElem;
import io.github.xpakx.habitcity.equipment.EquipmentEntryRepository;
import io.github.xpakx.habitcity.equipment.EquipmentService;
import io.github.xpakx.habitcity.equipment.UserEquipment;
import io.github.xpakx.habitcity.equipment.UserEquipmentRepository;
import io.github.xpakx.habitcity.money.MoneyRepository;
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
        cityBuildingRepository.deleteAll();
        buildingRepository.deleteAll();
        equipmentRepository.deleteAll();
        recipeRepository.deleteAll();
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
        recipe.setRequiredBuilding(buildingRepository.getReferenceById(building));
        recipe.setResource1(items.size()>0 ? resourceRepository.getReferenceById(items.get(0)) : null);
        recipe.setResource2(items.size()>1 ? resourceRepository.getReferenceById(items.get(1)) : null);
        recipe.setResource3(items.size()>2 ? resourceRepository.getReferenceById(items.get(2)) : null);
        recipe.setResource4(items.size()>3 ? resourceRepository.getReferenceById(items.get(3)) : null);
        recipe.setResource5(items.size()>4 ? resourceRepository.getReferenceById(items.get(4)) : null);
        recipe.setResource6(items.size()>5 ? resourceRepository.getReferenceById(items.get(5)) : null);
        recipe.setResource7(items.size()>6 ? resourceRepository.getReferenceById(items.get(6)) : null);
        recipe.setResource8(items.size()>7 ? resourceRepository.getReferenceById(items.get(7)) : null);
        recipe.setResource9(items.size()>8 ? resourceRepository.getReferenceById(items.get(8)) : null);
        recipe.setResource(resourceRepository.getReferenceById(item));
        recipeRepository.save(recipe);
    }

    private Long addBuilding(String name) {
        Building building = new Building();
        building.setName(name);
        building.setBaseCost(0);
        building.setRarity(0);
        return buildingRepository.save(building).getId();
    }


}