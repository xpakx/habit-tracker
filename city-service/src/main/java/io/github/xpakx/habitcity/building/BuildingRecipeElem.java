package io.github.xpakx.habitcity.building;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.habitcity.resource.Resource;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BuildingRecipeElem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    @JsonIgnore
    private Resource resource;
    private Integer amount;

    @ManyToOne
    @JoinColumn(name = "building_id")
    @JsonIgnore
    private Building building;
}
