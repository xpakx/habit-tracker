package io.github.xpakx.habittracker.habit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "context_id")
    private HabitContext context;

    @OneToOne
    @JoinColumn(name = "trigger_id")
    private HabitTrigger trigger;

    private Integer interval;
    private Integer dailyCompletions;
    private LocalDateTime start;
    private LocalDateTime nextDue;
}