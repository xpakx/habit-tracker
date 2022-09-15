package io.github.xpakx.habitcity.crafting;

import io.github.xpakx.habitcity.resource.Resource;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Resource resource1;
    @OneToOne
    private Resource resource2;
    @OneToOne
    private Resource resource3;
    @OneToOne
    private Resource resource4;
    @OneToOne
    private Resource resource5;
    @OneToOne
    private Resource resource6;
    @OneToOne
    private Resource resource7;
    @OneToOne
    private Resource resource8;
    @OneToOne
    private Resource resource9;
}
