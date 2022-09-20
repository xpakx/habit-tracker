package io.github.xpakx.habitcity.city;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityBuildingRepository extends JpaRepository<CityBuilding, Long> {
    long countByCityId(Long id);

    List<CityBuilding> findByCityIdAndCityUserId(Long cityId, Long userId);
}