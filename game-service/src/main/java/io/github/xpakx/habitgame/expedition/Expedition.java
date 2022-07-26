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
    @Column(name = "end_time")
    private LocalDateTime end;
    private boolean finished;
    @Column(name = "ret")
    private boolean returning;
    private LocalDateTime returnEnd;


    @ManyToOne
    @JoinColumn(name = "island_id")
    @JsonIgnore
    private Island destination;

    @OneToOne(mappedBy = "expedition")
    private ExpeditionResult expeditionResult;

}
