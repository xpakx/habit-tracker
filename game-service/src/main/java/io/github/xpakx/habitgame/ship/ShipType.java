package io.github.xpakx.habitgame.ship;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShipType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String imgUrl;
    private Integer baseSize;
    private Integer rarity;
    private String code;

    private Integer strength;
    private Integer criticalRate;
    private Integer hitRate;
    private Integer hp;
    private Integer movementRange;
    private Integer attackRange;
}
