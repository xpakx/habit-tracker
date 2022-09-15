package io.github.xpakx.habitcity.crafting.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CraftRequest {
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

    public List<CraftElem> asList() {
        List<CraftElem> result = new ArrayList<>();
        result.add(elem1);
        result.add(elem2);
        result.add(elem3);
        result.add(elem4);
        result.add(elem6);
        result.add(elem7);
        result.add(elem8);
        result.add(elem9);
        result.add(elem9);
        return result;
    }
}
