package io.github.xpakx.habitgamification.badge.processor;

import io.github.xpakx.habitgamification.badge.Badge;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BronzeAchievementProcessorImplTest {
    private BronzeAchievementProcessorImpl processor;

    @BeforeEach
    void setUp() {
        processor = new BronzeAchievementProcessorImpl();
    }

    @Test
    void shouldReturnBadgeType() {
        Badge result = processor.type();
        assertEquals(Badge.BRONZE, result);
    }

    @ParameterizedTest
    @ValueSource(ints = {1000, 1001, 3000, Integer.MAX_VALUE})
    void shouldReturnBadgeForExpHigherThanOrEqual1000(int experience) {
        HabitCompletionEvent event = new HabitCompletionEvent();
        Optional<Badge> result = processor.process(event, experience);
        assertTrue(result.isPresent());
        assertEquals(Badge.BRONZE, result.get());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 12, 999, Integer.MIN_VALUE, -1, -100})
    void shouldNotReturnBadgeForExpLowerThan1000(int experience) {
        HabitCompletionEvent event = new HabitCompletionEvent();
        Optional<Badge> result = processor.process(event, experience);
        assertTrue(result.isEmpty());
    }
}