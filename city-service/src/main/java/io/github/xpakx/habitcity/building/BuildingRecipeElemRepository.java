package io.github.xpakx.habitcity.building;

import io.github.xpakx.habitcity.building.dto.CraftBuildingElem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildingRecipeElemRepository extends JpaRepository<BuildingRecipeElem, Long> {
    List<BuildingRecipeElem> findByBuildingId(Long buildingId);
}