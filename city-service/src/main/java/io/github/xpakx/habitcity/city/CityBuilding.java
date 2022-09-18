package io.github.xpakx.habitcity.city;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.habitcity.building.Building;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CityBuilding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "city_id")
    @JsonIgnore
    private City city;

    @ManyToOne
    @JoinColumn(name = "building_id")
    @JsonIgnore
    private Building building;
}
