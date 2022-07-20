package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.HabitRequest;
import io.github.xpakx.habittracker.habit.dto.HabitUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/habit")
@RequiredArgsConstructor
public class HabitController {
    public final HabitService service;

    @PostMapping
    public ResponseEntity<Habit> addHabit(@RequestBody HabitRequest request) {
        return new ResponseEntity<>(
                service.addHabit(request),
                HttpStatus.OK
        );
    }

    @PutMapping("/{habitId}")
    public ResponseEntity<Habit> updateHabit(@RequestBody HabitUpdateRequest request, @PathVariable Long habitId) {
        return new ResponseEntity<>(
                service.updateHabit(habitId, request),
                HttpStatus.OK
        );
    }
}
