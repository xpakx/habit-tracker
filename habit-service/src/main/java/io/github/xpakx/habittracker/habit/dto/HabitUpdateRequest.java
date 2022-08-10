package io.github.xpakx.habittracker.habit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HabitUpdateRequest {
    private String name;
    private String description;
    private Integer interval;
    private Integer dailyCompletions;
    private LocalDateTime start;
    private Integer difficulty;
}
