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

    private Integer xPos;
    private Integer yPos;

    @OneToOne
    @JoinColumn(name = "ship_id")
    private Ship ship;

    @ManyToOne
    private Battle battle;
}