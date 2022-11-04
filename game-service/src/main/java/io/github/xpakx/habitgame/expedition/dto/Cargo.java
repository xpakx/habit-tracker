package io.github.xpakx.habitgame.expedition.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cargo {
    private Long resourceId;
    private Integer amount;
    private String name;
    private String code;
    private Integer rarity;
}
