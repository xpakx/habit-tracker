package io.github.xpakx.habitcity.city;

import io.github.xpakx.habitcity.building.Building;
import io.github.xpakx.habitcity.building.BuildingRepository;
import io.github.xpakx.habitcity.city.dto.BuildingRequest;
import io.github.xpakx.habitcity.city.dto.BuildingResponse;
import io.github.xpakx.habitcity.city.error.NotEnoughSpaceException;
import io.github.xpakx.habitcity.equipment.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final CityBuildingRepository cityBuildingRepository;
    private final BuildingRepository buildingRepository;
    private final EquipmentService equipmentService;
    private final UserEquipmentRepository equipmentRepository;
    private final EquipmentEntryRepository entryRepository;

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

        // TODO: subtract resources

        City city = cityRepository.findByIdAndUserId(cityId, userId).orElseThrow();
        long buildingsInCity = cityBuildingRepository.countByCityId(city.getId());
        if(buildingsInCity >= city.getMaxSize()) {
            throw new NotEnoughSpaceException();
        }

        CityBuilding cityBuilding = new CityBuilding();
        cityBuilding.setBuilding(building);
        cityBuilding.setCity(city);
        cityBuildingRepository.save(cityBuilding);

        return null;
    }
}
