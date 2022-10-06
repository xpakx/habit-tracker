package io.github.xpakx.habitgamification.badge.processor;

import io.github.xpakx.habitgamification.badge.Badge;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GoldAchievementProcessorImplTest {
    private AchievementProcessor processor;
    private final static Random rnd = new Random(210832879274L);

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

    @ParameterizedTest
    @MethodSource("provideRandomEvents")
    void eventContentShouldNotInfluenceResultIfExpHigherThan5000(HabitCompletionEvent event) {
        Optional<Badge> result = processor.process(event, 15000);
        assertTrue(result.isPresent());
        assertEquals(Badge.GOLD, result.get());
    }

    @ParameterizedTest
    @MethodSource("provideRandomEvents")
    void eventContentShouldNotInfluenceResultIfExpLowerThan5000(HabitCompletionEvent event) {
        Optional<Badge> result = processor.process(event, 10);
        assertTrue(result.isEmpty());
    }

    private static Stream<Arguments> provideRandomEvents() {
        return Stream.of(
                Arguments.of(constructRandomEvent()),
                Arguments.of(constructRandomEvent()),
                Arguments.of(constructRandomEvent()),
                Arguments.of(constructRandomEvent()),
                Arguments.of(constructRandomEvent()),
                Arguments.of(constructRandomEvent())
        );
    }

    private static HabitCompletionEvent constructRandomEvent() {
        HabitCompletionEvent event = new HabitCompletionEvent();
        event.setCompletionId(rnd.nextLong());
        event.setUserId(rnd.nextLong());
        event.setHabitId(rnd.nextLong());
        event.setDifficulty(rnd.nextInt());
        return event;
    }

}