package io.github.xpakx.habitgame.expedition;

import io.github.xpakx.habitgame.battle.Position;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long shipId;
    private Long userId;
    private String name;
    private String code;
    private Integer maxCargo;
    private Integer rarity;
    private Integer size;

    @ManyToOne
    @JoinColumn(name = "expedition_id")
    private Expedition expedition;

    private boolean destroyed;
    private boolean damaged;
    private boolean prepared;

    private boolean action;
    private boolean movement;

    private boolean enemy;
    private boolean boss;

    private Integer strength;
    private Integer criticalRate;
    private Integer hitRate;
    private Integer hp;

    private Integer movementRange;
    private Integer attackRange;

    @OneToOne(mappedBy = "ship", orphanRemoval = true)
    private Position position;
}
