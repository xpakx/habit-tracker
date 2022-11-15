package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.equipment.dto.EquipmentEntrySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EquipmentEntryRepository extends JpaRepository<EquipmentEntry, Long> {
    List<EquipmentEntry> findByEquipmentIdAndResourceId(Long equipmentId, Long resourceId);
    List<EquipmentEntry> findByEquipmentIdAndBuildingId(Long equipmentId, Long buildingId);
    List<EquipmentEntry> findByEquipmentIdAndShipId(Long equipmentId, Long shipId);
    long countByEquipmentId(Long id);

    List<EquipmentEntrySummary> findByEquipmentId(Long id);

    List<EquipmentEntry> getByEquipmentId(Long id);

    List<EquipmentEntrySummary> findByEquipmentIdAndBuildingIsNotNull(Long id);
    List<EquipmentEntrySummary> findByEquipmentIdAndShipIsNotNull(Long id);

    Optional<EquipmentEntry> findByIdAndEquipmentUserId(Long id, Long userId);
}