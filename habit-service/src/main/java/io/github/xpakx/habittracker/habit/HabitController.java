package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.DayRequest;
import io.github.xpakx.habittracker.habit.dto.HabitRequest;
import io.github.xpakx.habittracker.habit.dto.HabitUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habit")
@RequiredArgsConstructor
public class HabitController {
    public final HabitService service;

    @PostMapping
    public ResponseEntity<Habit> addHabit(@RequestBody HabitRequest request) {
        return new ResponseEntity<>(
                service.addHabit(request),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{habitId}")
    public ResponseEntity<Habit> updateHabit(@RequestBody HabitUpdateRequest request, @PathVariable Long habitId) {
        return new ResponseEntity<>(
                service.updateHabit(habitId, request),
                HttpStatus.OK
        );
    }

    @GetMapping("/date")
    public ResponseEntity<List<Habit>> getHabitsForDay(@RequestBody DayRequest request) {
        return new ResponseEntity<>(
                service.getHabitsForDay(request),
                HttpStatus.OK
        );
    }
}
