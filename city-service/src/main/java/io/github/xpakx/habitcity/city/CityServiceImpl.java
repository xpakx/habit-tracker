package io.github.xpakx.habitcity.city;

import io.github.xpakx.habitcity.building.BuildingRepository;
import io.github.xpakx.habitcity.city.dto.BuildingRequest;
import io.github.xpakx.habitcity.city.dto.BuildingResponse;
import io.github.xpakx.habitcity.equipment.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final CityBuildingRepository cityBuildingRepository;
    private final BuildingRepository buildingRepository;
    private final EquipmentService equipmentService;

    @Override
    public BuildingResponse build(BuildingRequest request, Long cityId, Long userId) {
        return null;
    }
}
