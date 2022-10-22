package io.github.xpakx.habitcity.city;

import io.github.xpakx.habitcity.city.dto.CityBuildingDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityBuildingRepository extends JpaRepository<CityBuilding, Long> {
    long countByCityId(Long id);

    List<CityBuildingDetails> findByCityIdAndCityUserId(Long cityId, Long userId);
}