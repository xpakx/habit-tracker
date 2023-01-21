package io.github.xpakx.habitgame.battle;

import io.github.xpakx.habitgame.expedition.Ship;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer x;
    private Integer y;

    @OneToOne
    @JoinColumn(name = "ship_id")
    private Ship ship;

    @OneToOne
    @JoinColumn(name = "terrain_type_id")
    private TerrainType terrain;

    @ManyToOne
    private Battle battle;
}