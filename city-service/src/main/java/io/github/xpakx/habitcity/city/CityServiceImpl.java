package io.github.xpakx.habitcity.city;

import io.github.xpakx.habitcity.building.Building;
import io.github.xpakx.habitcity.building.dto.BuildingCraftList;
import io.github.xpakx.habitcity.city.dto.BuildingRequest;
import io.github.xpakx.habitcity.city.dto.BuildingResponse;
import io.github.xpakx.habitcity.city.error.NotEnoughSpaceException;
import io.github.xpakx.habitcity.equipment.*;
import io.github.xpakx.habitcity.equipment.dto.AccountEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final CityBuildingRepository cityBuildingRepository;
    private final EquipmentService equipmentService;
    private final UserEquipmentRepository equipmentRepository;
    private final EquipmentEntryRepository entryRepository;
    private final BuildingRecipeService recipeService;

    @Override
    public BuildingResponse build(BuildingRequest request, Long cityId, Long userId) {
        UserEquipment eq = equipmentRepository.getByUserId(userId).orElseThrow();
        List<EquipmentEntry> eqEntries = entryRepository.getByEquipmentId(eq.getId());

        Building building = eqEntries.stream()
                .map(EquipmentEntry::getBuilding)
                .filter(Objects::nonNull)
                .filter((a) -> Objects.equals(a.getId(), request.getBuildingId()))
                .findFirst()
                .orElseThrow();

        BuildingCraftList craftList = new BuildingCraftList(1, recipeService.getRecipe(request.getBuildingId()));
        equipmentService.subtractResources(craftList, eqEntries);

        City city = cityRepository.findByIdAndUserId(cityId, userId).orElseThrow();
        long buildingsInCity = cityBuildingRepository.countByCityId(city.getId());
        if(buildingsInCity >= city.getMaxSize()) {
            throw new NotEnoughSpaceException();
        }

        CityBuilding cityBuilding = new CityBuilding();
        cityBuilding.setBuilding(building);
        cityBuilding.setCity(city);
        cityBuildingRepository.save(cityBuilding);
        entryRepository.saveAll(eqEntries);

        return null;
    }

    @Override
    public List<City> getCities(Long userId) {
        return cityRepository.findByUserId(userId);
    }

    @Override
    public List<CityBuilding> getBuildings(Long cityId, Long userId) {
        return cityBuildingRepository.findByCityIdAndCityUserId(cityId, userId);
    }

    @Override
    public void addUserCity(AccountEvent event) {
        City shop = new City();
        shop.setUserId(event.getId());
        shop.setMaxSize(15);
        cityRepository.save(shop);
    }
}
