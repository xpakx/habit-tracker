package io.github.xpakx.habitcity.equipment.dto;

import io.github.xpakx.habitcity.equipment.EquipmentEntry;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EquipmentResponse {
    List<EquipmentEntrySummary> items;
}
