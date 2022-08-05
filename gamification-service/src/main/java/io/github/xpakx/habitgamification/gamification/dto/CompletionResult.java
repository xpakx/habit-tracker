package io.github.xpakx.habitgamification.gamification.dto;

import io.github.xpakx.habitgamification.gamification.Badge;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class CompletionResult {
    int experience;
    List<Badge> achievements;
}
