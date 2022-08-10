package io.github.xpakx.habittracker.habit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class HabitDetails {
    private Long id;
    private String name;
    private String description;
    private Integer interval;
    private Integer dailyCompletions;
    private LocalDateTime start;
    private LocalDateTime nextDue;

    private HabitContextMin context;
    private HabitTriggerMin trigger;
    private Integer difficulty;
}
