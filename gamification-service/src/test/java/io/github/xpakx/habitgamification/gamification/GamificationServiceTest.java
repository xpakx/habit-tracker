package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.badge.Achievement;
import io.github.xpakx.habitgamification.badge.AchievementRepository;
import io.github.xpakx.habitgamification.badge.Badge;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletionEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GamificationServiceTest {
    private Long userId;
    @Autowired
    private GamificationService service;
    @Autowired
    private ExpEntryRepository expRepository;
    @Autowired
    private AchievementRepository achievementRepository;

    @BeforeEach
    void setUp() {
        userId = 1L;
    }

    @AfterEach
    void tearDown() {
        expRepository.deleteAll();
        achievementRepository.deleteAll();
    }

    @Test
    void shouldAddCompletion() {
        HabitCompletionEvent event = getEvent();
        service.newAttempt(event);
        List<ExpEntry> result = expRepository.findAll();
        assertEquals(1, result.size());
    }

    private HabitCompletionEvent getEvent() {
        return getEvent(0);
    }

    private HabitCompletionEvent getEvent(int difficulty) {
        HabitCompletionEvent event = new HabitCompletionEvent();
        event.setUserId(userId);
        event.setCompletionId(1L);
        event.setDifficulty(difficulty);
        event.setHabitId(1L);
        return event;
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 12, 994})
    void shouldNotReturnBadges(int initialExp) {
        addExpEntries(initialExp);
        HabitCompletionEvent event = getEvent();
        service.newAttempt(event);
        List<Achievement> result = achievementRepository.findAll();
        assertEquals(0, result.size());
    }

    private void addExpEntries(int exp) {
        int entriesToAdd = exp/5;
        int lastEventExp = exp%5;
        List<ExpEntry> entries = new ArrayList<>();
        for(int i=0; i<entriesToAdd; i++) {
            entries.add(getExpEntry(5));
        }
        if(lastEventExp > 0) {
            entries.add(getExpEntry(lastEventExp));
        }
        expRepository.saveAll(entries);
    }

    private ExpEntry getExpEntry(int exp) {
        ExpEntry entry = new ExpEntry();
        entry.setUserId(userId);
        entry.setExperience(exp);
        return entry;
    }

    @ParameterizedTest
    @ValueSource(ints = {995, 999, 1000, 1001})
    void shouldAddBadge(int initialExp) {
        addExpEntries(initialExp);
        HabitCompletionEvent event = getEvent();
        service.newAttempt(event);
        List<Achievement> result = achievementRepository.findAll();
        assertEquals(1, result.size());
        assertEquals(result.get(0).getBadgeType(), Badge.BRONZE);
    }

    @Test
    void shouldNotAddAlreadyAddedBadge() {
        addExpEntries(1000);
        addAchievement(Badge.BRONZE);
        HabitCompletionEvent event = getEvent();
        service.newAttempt(event);
        List<Achievement> result = achievementRepository.findAll();
        assertEquals(1, result.size());
    }

    private void addAchievement(Badge badge) {
        Achievement achievement = new Achievement();
        achievement.setBadgeType(badge);
        achievementRepository.save(achievement);
    }
}