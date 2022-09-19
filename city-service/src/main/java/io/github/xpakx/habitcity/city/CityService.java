package io.github.xpakx.habitcity.city;

import io.github.xpakx.habitcity.city.dto.BuildingRequest;
import io.github.xpakx.habitcity.city.dto.BuildingResponse;

public interface CityService {
    BuildingResponse build(BuildingRequest request, Long cityId, Long userId);
}
