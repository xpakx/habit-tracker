package io.github.xpakx.habitcity.crafting.dto;

import io.github.xpakx.habitcity.equipment.dto.CraftElem;
import io.github.xpakx.habitcity.equipment.dto.CraftList;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CraftRequest implements CraftList {
    private CraftElem elem1;
    private CraftElem elem2;
    private CraftElem elem3;
    private CraftElem elem4;
    private CraftElem elem5;
    private CraftElem elem6;
    private CraftElem elem7;
    private CraftElem elem8;
    private CraftElem elem9;
    private Integer amount;

    @Override
    public List<CraftElem> asCraftList() {
        List<CraftElem> result = new ArrayList<>();
        addElemIfNotEmpty(result, elem1);
        addElemIfNotEmpty(result, elem2);
        addElemIfNotEmpty(result, elem3);
        addElemIfNotEmpty(result, elem4);
        addElemIfNotEmpty(result, elem5);
        addElemIfNotEmpty(result, elem6);
        addElemIfNotEmpty(result, elem7);
        addElemIfNotEmpty(result, elem8);
        addElemIfNotEmpty(result, elem9);
        return result;
    }

    private void addElemIfNotEmpty(List<CraftElem> result, CraftElem elem) {
        if(elem != null && elem.getId() != null) {
            result.add(elem);
        }
    }
}
