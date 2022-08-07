package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.gamification.dto.CompletionResult;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GamificationServiceImpl implements GamificationService {
    private final ExpEntryRepository expRepository;
    private final AchievementRepository achievementRepository;
    private final List<AchievementProcessor> processors;

    @Override
    public CompletionResult newAttempt(HabitCompletion completion) {
        ExpEntry exp = new ExpEntry();
        exp.setCompletionId(completion.getCompletionId());
        exp.setDate(LocalDateTime.now());
        exp.setExperience(10);
        exp.setUserId(completion.getUserId());
        expRepository.save(exp);
        List<Achievement> achievements = processForAchievements(completion, exp);
        return new CompletionResult(
                exp.getExperience(),
                achievements.stream()
                        .map(Achievement::getBadgeType)
                        .collect(Collectors.toList())
        );
    }

    private List<Achievement> processForAchievements(HabitCompletion completion, ExpEntry exp) {
        int expSum = exp.getExperience(); //TODO
        List<Achievement> achievements = processors.stream()
                .map((p) -> p.process(completion, expSum))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::badgeToAchievement)
                .toList();
        achievementRepository.saveAll(achievements);
        return achievements;
    }

    private Achievement badgeToAchievement(Badge badge) {
        Achievement achievement = new Achievement();
        achievement.setBadgeType(badge);
        achievement.setDate(LocalDateTime.now());
        return achievement;
    }
}
