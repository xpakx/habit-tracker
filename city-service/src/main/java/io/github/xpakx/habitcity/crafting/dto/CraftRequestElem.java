package io.github.xpakx.habitcity.crafting.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.habitcity.equipment.dto.CraftElem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CraftRequestElem implements CraftElem {
    private Long id;

    @JsonIgnore
    @Override
    public Integer getAmount() {
        return 1;
    }
}
