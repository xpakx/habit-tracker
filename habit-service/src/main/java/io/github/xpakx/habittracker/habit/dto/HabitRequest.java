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
public class HabitRequest {
    private String name;
    private String description;
    private Integer interval;
    private LocalDateTime start;

    private Long contextId;
    private String triggerName;
}
