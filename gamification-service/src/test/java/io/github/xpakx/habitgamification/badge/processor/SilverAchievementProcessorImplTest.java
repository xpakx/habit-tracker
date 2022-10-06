package io.github.xpakx.habitgamification.badge.processor;

import io.github.xpakx.habitgamification.badge.Badge;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SilverAchievementProcessorImplTest {
    private AchievementProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new SilverAchievementProcessorImpl();
    }

    @Test
    void shouldReturnBadgeType() {
        Badge result = processor.type();
        assertEquals(Badge.SILVER, result);
    }

    @ParameterizedTest
    @ValueSource(ints = {5000, 5001, 7000, Integer.MAX_VALUE})
    void shouldReturnBadgeForExpHigherThanOrEqual5000(int experience) {
        HabitCompletionEvent event = new HabitCompletionEvent();
        Optional<Badge> result = processor.process(event, experience);
        assertTrue(result.isPresent());
        assertEquals(Badge.SILVER, result.get());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 12, 4999, Integer.MIN_VALUE, -1, -100})
    void shouldNotReturnBadgeForExpLowerThan5000(int experience) {
        HabitCompletionEvent event = new HabitCompletionEvent();
        Optional<Badge> result = processor.process(event, experience);
        assertTrue(result.isEmpty());
    }

}