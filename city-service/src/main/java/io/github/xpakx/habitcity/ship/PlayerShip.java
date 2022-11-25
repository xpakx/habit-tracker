package io.github.xpakx.habitcity.ship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.habitcity.city.City;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PlayerShip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ship_id")
    @JsonIgnore
    private Ship ship;

    @ManyToOne
    @JoinColumn(name = "city_id")
    @JsonIgnore
    private City city;

    private boolean blocked;
    private boolean damaged;
}