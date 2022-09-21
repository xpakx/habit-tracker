package io.github.xpakx.habitcity.city;

import io.github.xpakx.habitcity.city.dto.BuildingRequest;
import io.github.xpakx.habitcity.city.dto.BuildingResponse;
import io.github.xpakx.habitcity.equipment.dto.AccountEvent;

import java.util.List;

public interface CityService {
    BuildingResponse build(BuildingRequest request, Long cityId, Long userId);
    List<City> getCities(Long userId);
    List<CityBuilding> getBuildings(Long cityId, Long userId);
    void addUserCity(AccountEvent event);
    boolean hasUserBuilding(Long buildingId, Long userId);
}
