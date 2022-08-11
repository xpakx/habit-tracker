package io.github.xpakx.habitgamification.gamification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HabitCompletionEvent {
    private Long completionId;
    private Long habitId;
    private Long userId;
    private Integer difficulty;
}
