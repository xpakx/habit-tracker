package io.github.xpakx.habitcity.ship;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Ship implements Serializable {
    @Serial
    private static final long serialVersionUID = 120701839676376377L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String imgUrl;
    private Integer size;
    private Integer maxCargo;
    private Integer baseCost;
    private Integer rarity;
    private String code;
    private Integer strength;
    private Integer hitRate;
    private Integer criticalRate;
}
