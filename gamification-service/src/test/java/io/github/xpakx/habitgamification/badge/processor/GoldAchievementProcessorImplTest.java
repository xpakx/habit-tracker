package io.github.xpakx.habitgamification.badge.processor;

import io.github.xpakx.habitgamification.badge.Badge;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GoldAchievementProcessorImplTest {
    private AchievementProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new GoldAchievementProcessorImpl();
    }

    @Test
    void shouldReturnBadgeType() {
        Badge result = processor.type();
        assertEquals(Badge.GOLD, result);
    }

    @ParameterizedTest
    @ValueSource(ints = {15000, 15001, 17000, Integer.MAX_VALUE})
    void shouldReturnBadgeForExpHigherThanOrEqual15000(int experience) {
        HabitCompletionEvent event = new HabitCompletionEvent();
        Optional<Badge> result = processor.process(event, experience);
        assertTrue(result.isPresent());
        assertEquals(Badge.GOLD, result.get());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 48, 14999, Integer.MIN_VALUE, -1, -100})
    void shouldNotReturnBadgeForExpLowerThan15000(int experience) {
        HabitCompletionEvent event = new HabitCompletionEvent();
        Optional<Badge> result = processor.process(event, experience);
        assertTrue(result.isEmpty());
    }

}