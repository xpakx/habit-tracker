package io.github.xpakx.habitcity.crafting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.habitcity.resource.Resource;
import io.github.xpakx.habitcity.ship.Ship;
import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Recipe implements Serializable {
    @Serial
    private static final long serialVersionUID = 715214808488882315L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "res1_id")
    @JsonIgnore
    private Resource resource1;

    @ManyToOne
    @JoinColumn(name = "res2_id")
    @JsonIgnore
    private Resource resource2;

    @ManyToOne
    @JoinColumn(name = "res3_id")
    @JsonIgnore
    private Resource resource3;

    @ManyToOne
    @JoinColumn(name = "res4_id")
    @JsonIgnore
    private Resource resource4;

    @ManyToOne
    @JoinColumn(name = "res5_id")
    @JsonIgnore
    private Resource resource5;

    @ManyToOne
    @JoinColumn(name = "res6_id")
    @JsonIgnore
    private Resource resource6;

    @ManyToOne
    @JoinColumn(name = "res7_id")
    @JsonIgnore
    private Resource resource7;

    @ManyToOne
    @JoinColumn(name = "res8_id")
    @JsonIgnore
    private Resource resource8;

    @ManyToOne
    @JoinColumn(name = "res9_id")
    @JsonIgnore
    private Resource resource9;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    @JsonIgnore
    private Resource resource;

    @ManyToOne
    @JoinColumn(name = "ship_id")
    @JsonIgnore
    private Ship ship;

}
