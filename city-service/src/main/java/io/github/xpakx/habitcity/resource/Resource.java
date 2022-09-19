package io.github.xpakx.habitcity.resource;

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
public class Resource implements Serializable {
    @Serial
    private static final long serialVersionUID = -5833214018665842925L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer maxStock;
    private String imgUrl;
    private Integer baseCost;
    private Integer rarity;
    private String code;
}
