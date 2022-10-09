package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.badge.Achievement;
import io.github.xpakx.habitgamification.badge.processor.AchievementProcessor;
import io.github.xpakx.habitgamification.badge.AchievementRepository;
import io.github.xpakx.habitgamification.badge.Badge;
import io.github.xpakx.habitgamification.gamification.dto.CompletionResult;
import io.github.xpakx.habitgamification.gamification.dto.ExpResponse;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GamificationServiceImpl implements GamificationService {
    private final ExpEntryRepository expRepository;
    private final AchievementRepository achievementRepository;
    private final List<AchievementProcessor> processors;

    @Override
    public CompletionResult newAttempt(HabitCompletionEvent completion) {
        ExpEntry exp = new ExpEntry();
        exp.setCompletionId(completion.getCompletionId());
        exp.setDate(LocalDateTime.now());
        exp.setExperience(difficultyToExperience(completion.getDifficulty()));
        exp.setUserId(completion.getUserId());
        expRepository.save(exp);
        int experience = expRepository.getExpForUser(completion.getUserId());
        List<Achievement> achievements = processForAchievements(completion, experience);
        return new CompletionResult(
                experience,
                achievements.stream()
                        .map(Achievement::getBadgeType)
                        .collect(Collectors.toList())
        );
    }

    private int difficultyToExperience(Integer difficulty) {
        if(difficulty == null) {
            return 5;
        }
        if(difficulty >= 0 && difficulty <=3) {
            return (difficulty+1) * 5;
        }
        if(difficulty > 3) {
            return 20;
        }
        return 0;
    }

    private List<Achievement> processForAchievements(HabitCompletionEvent completion, int expSum) {
        Set<Badge> alreadyAddedBadges = achievementRepository.findAllByUserId(completion.getUserId())
                .stream()
                .map(Achievement::getBadgeType)
                .collect(Collectors.toSet());
        List<Achievement> achievements = processors.stream()
                .map((p) -> p.process(completion, expSum))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter((p) -> !alreadyAddedBadges.contains(p))
                .map((q) -> badgeToAchievement(q, completion.getUserId()))
                .toList();
        achievementRepository.saveAll(achievements);
        return achievements;
    }

    private Achievement badgeToAchievement(Badge badge, Long userId) {
        Achievement achievement = new Achievement();
        achievement.setBadgeType(badge);
        achievement.setUserId(userId);
        achievement.setDate(LocalDateTime.now());
        return achievement;
    }

    @Override
    public ExpResponse getExp(Long userId) {
        Integer experience = expRepository.getExpForUser(userId);
        ExpResponse response = new ExpResponse();
        response.setExperience(experience != null ? experience : 0);
        return  response;
    }
}
