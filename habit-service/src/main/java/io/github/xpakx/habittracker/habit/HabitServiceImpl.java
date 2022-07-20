package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.HabitRequest;
import io.github.xpakx.habittracker.habit.dto.HabitUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {
    private final HabitRepository habitRepository;
    private final HabitContextRepository contextRepository;

    @Transactional
    public Habit addHabit(HabitRequest request) {
        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        habit.setInterval(request.getInterval());
        habit.setStart(request.getStart());
        habit.setNextDue(request.getStart());
        habit.setContext(contextRepository.getReferenceById(request.getContextId()));
        HabitTrigger trigger = new HabitTrigger();
        trigger.setName(request.getTriggerName());
        habit.setTrigger(trigger);
        return habitRepository.save(habit);
    }

    @Override
    public Habit updateHabit(Long habitId, HabitUpdateRequest request) {
        Habit habit = habitRepository.findById(habitId).orElseThrow();
        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        habit.setInterval(request.getInterval());
        habit.setStart(request.getStart());
        return null;
    }
}
