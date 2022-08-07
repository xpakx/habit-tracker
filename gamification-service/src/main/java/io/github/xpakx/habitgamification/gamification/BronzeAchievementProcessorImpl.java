package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.gamification.dto.HabitCompletion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BronzeAchievementProcessorImpl implements AchievementProcessor {
    @Override
    public Optional<Badge> process(HabitCompletion completion, int experience) {
        if(experience >= 50) {
            return Optional.of(type());
        }
        return Optional.empty();
    }

    @Override
    public Badge type() {
        return Badge.BRONZE;
    }
}
