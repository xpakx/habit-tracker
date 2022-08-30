package io.github.xpakx.habitcity.money;

import io.github.xpakx.habitcity.equipment.UserEquipment;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Money {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;
    @OneToOne(mappedBy = "money")
    private UserEquipment equipment;
}
