package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.gamification.dto.CompletionResult;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GamificationServiceImpl implements GamificationService {
    private final ExpEntryRepository expRepository;
    private final AchievementRepository achievementRepository;
    
    @Override
    public CompletionResult newAttempt(HabitCompletion completion) {
        return null;
    }
}
