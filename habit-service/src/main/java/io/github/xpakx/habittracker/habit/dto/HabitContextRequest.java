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
public class HabitContextRequest {
    private String name;
    private String description;
    private boolean timeBounded;
    private LocalDateTime activeStart;
    private LocalDateTime activeEnd;
}
