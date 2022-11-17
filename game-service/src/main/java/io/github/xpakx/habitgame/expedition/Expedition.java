package io.github.xpakx.habitgame.expedition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.habitgame.island.Island;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Expedition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean finished;

    @ManyToOne
    @JoinColumn(name = "island_id")
    @JsonIgnore
    private Island destination;

    @OneToOne(mappedBy = "expedition")
    private ExpeditionResult expeditionResult;

}
