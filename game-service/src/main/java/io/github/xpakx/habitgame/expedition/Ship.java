package io.github.xpakx.habitgame.expedition;

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

}
