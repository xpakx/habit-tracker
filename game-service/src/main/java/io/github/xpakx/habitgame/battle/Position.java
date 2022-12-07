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

    private Integer x1;
    private Integer y1;
    private Integer x2;
    private Integer y2;

    @OneToOne(mappedBy = "position", orphanRemoval = true)
    private Ship ship;
}