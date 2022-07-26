package io.github.xpakx.habitgamification.gamification.dto;

import io.github.xpakx.habitgamification.badge.Badge;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class CompletionResult {
    int experience;
    List<Badge> achievements;
}
