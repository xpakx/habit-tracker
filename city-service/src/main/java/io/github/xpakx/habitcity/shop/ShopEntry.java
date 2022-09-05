package io.github.xpakx.habitcity.shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.habitcity.building.Building;
import io.github.xpakx.habitcity.equipment.UserEquipment;
import io.github.xpakx.habitcity.resource.Resource;
import io.github.xpakx.habitcity.ship.Ship;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShopEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer amount;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    @JsonIgnore
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "building_id")
    @JsonIgnore
    private Building building;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    @JsonIgnore
    private Resource resource;

    @ManyToOne
    @JoinColumn(name = "ship_id")
    @JsonIgnore
    private Ship ship;
}
