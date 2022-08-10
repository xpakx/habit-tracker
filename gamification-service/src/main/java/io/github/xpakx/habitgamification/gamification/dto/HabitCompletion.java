package io.github.xpakx.habitgamification.gamification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HabitCompletion {
    private Long completionId;
    private Long habitId;
    private Long userId;
}
