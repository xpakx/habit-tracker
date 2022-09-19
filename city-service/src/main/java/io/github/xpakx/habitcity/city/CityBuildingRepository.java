package io.github.xpakx.habitcity.city;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CityBuildingRepository extends JpaRepository<CityBuilding, Long> {
    long countByCityId(Long id);
}