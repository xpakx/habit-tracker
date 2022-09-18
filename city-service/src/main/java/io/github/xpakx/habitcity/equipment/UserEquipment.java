package io.github.xpakx.habitcity.equipment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.habitcity.money.Money;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Integer maxSize;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "money_id")
    @JsonIgnore
    private Money money;
}
