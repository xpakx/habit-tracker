package io.github.xpakx.habitcity.equipment.dto;

import java.util.List;

public interface CraftList {
    List<CraftElem> asCraftList();
    Integer getAmount();
}
