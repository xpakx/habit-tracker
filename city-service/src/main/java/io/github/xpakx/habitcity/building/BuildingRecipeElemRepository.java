package io.github.xpakx.habitcity.building;

import io.github.xpakx.habitcity.building.dto.CraftBuildingElem;
import io.github.xpakx.habitcity.equipment.dto.CraftElem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildingRecipeElemRepository extends JpaRepository<BuildingRecipeElem, Long> {
    List<CraftBuildingElem> findByBuildingId(Long buildingId);
}