package io.github.xpakx.habittracker.habit.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public interface HabitDetails {
    Long getId();
    String getName();
    String getDescription();
    Integer getInterval();
    Integer getDailyCompletions();
    LocalDateTime getStart();
    LocalDateTime getNextDue();

    HabitContextMin getContext();
    HabitTriggerMin getTrigger();
    Integer getDifficulty();
}
