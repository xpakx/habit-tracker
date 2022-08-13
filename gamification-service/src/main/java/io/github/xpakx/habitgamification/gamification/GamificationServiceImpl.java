package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.gamification.dto.CompletionResult;
import io.github.xpakx.habitgamification.gamification.dto.ExpResponse;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletionEvent;
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
                exp.getExperience(),
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
            return difficulty * 5;
        }
        if(difficulty > 3) {
            return 15;
        }
        return 0;
    }

    private List<Achievement> processForAchievements(HabitCompletionEvent completion, int expSum) {
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

    @Override
    public ExpResponse getExp(Long userId) {
        Integer experience = expRepository.getExpForUser(userId);
        ExpResponse response = new ExpResponse();
        response.setExperience(experience != null ? experience : 0);
        return  response;
    }
}
