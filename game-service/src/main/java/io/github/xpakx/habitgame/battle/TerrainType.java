package io.github.xpakx.habitgame.battle;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class TerrainType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean blocked;
    private Integer move;
    private Integer defenceBonus;
    private Integer attackBonus;
}