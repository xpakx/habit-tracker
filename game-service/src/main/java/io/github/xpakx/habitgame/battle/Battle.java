package io.github.xpakx.habitgame.battle;

import io.github.xpakx.habitgame.expedition.Expedition;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Battle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "expedition_id")
    private Expedition expedition;

    private boolean finished;
    private boolean started;
    private Integer height;
    private Integer width;
    private BattleObjective objective;
    private Integer turn;
}