package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.gamification.dto.HabitCompletionEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GamificationServiceTest {
    private Long userId;
    @Autowired
    private GamificationService service;
    @Autowired
    private ExpEntryRepository expRepository;

    @BeforeEach
    void setUp() {
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        expRepository.deleteAll();
    }

    @Test
    void shouldAddCompletion() {
        HabitCompletionEvent event = getEvent();
        service.newAttempt(event);
        List<ExpEntry> result = expRepository.findAll();
        assertEquals(1, result.size());
    }

    private HabitCompletionEvent getEvent() {
        HabitCompletionEvent event = new HabitCompletionEvent();
        event.setUserId(userId);
        event.setCompletionId(1L);
        event.setDifficulty(10);
        event.setHabitId(1L);
        return event;
    }
}