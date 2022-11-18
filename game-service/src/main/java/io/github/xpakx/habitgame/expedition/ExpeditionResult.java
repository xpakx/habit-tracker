package io.github.xpakx.habitgame.expedition;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class ExpeditionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "expedition_id")
    private Expedition expedition;

    private ResultType type;
    private boolean completed;

}