package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.equipment.dto.EquipmentEntrySummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentEntryRepository extends JpaRepository<EquipmentEntry, Long> {
    List<EquipmentEntry> findByEquipmentIdAndResourceId(Long equipmentId, Long resourceId);
    List<EquipmentEntry> findByEquipmentIdAndBuildingId(Long equipmentId, Long buildingId);
    List<EquipmentEntry> findByEquipmentIdAndShipId(Long equipmentId, Long shipId);
    long countByEquipmentId(Long id);

    List<EquipmentEntrySummary> findByEquipmentId(Long id);

    List<EquipmentEntry> getByEquipmentId(Long id);
}