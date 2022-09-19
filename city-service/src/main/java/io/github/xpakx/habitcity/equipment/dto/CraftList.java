package io.github.xpakx.habitcity.equipment.dto;

import io.github.xpakx.habitcity.crafting.dto.CraftElem;

import java.util.List;

public interface CraftList {
    List<CraftElem> asCraftList();
    int getAmount();
}
