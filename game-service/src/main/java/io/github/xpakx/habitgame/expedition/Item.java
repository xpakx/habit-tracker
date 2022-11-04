package io.github.xpakx.habitgame.expedition;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long resourceId;
    private Integer amount;
    private String name;
    private String code;
    private Integer rarity;

    @ManyToOne
    @JoinColumn(name = "ship_id")
    private Ship ship;

}
