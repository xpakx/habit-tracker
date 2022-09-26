package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.HabitRequest;
import io.github.xpakx.habittracker.habit.dto.HabitUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/habit")
@RequiredArgsConstructor
public class HabitController {
    public final HabitService service;

    @PostMapping
    public ResponseEntity<Habit> addHabit(@RequestBody HabitRequest request, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.addHabit(request, Long.valueOf(id)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{habitId}")
    public ResponseEntity<Habit> updateHabit(@RequestBody HabitUpdateRequest request, @PathVariable Long habitId, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.updateHabit(habitId, request, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @GetMapping(value = "/date")
    public ResponseEntity<List<Habit>> getHabitsForDay(@RequestParam("date") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate date, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.getHabitsForDay(date, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @GetMapping("/daily")
    public ResponseEntity<List<Habit>> getDailyHabits(@RequestHeader String id) {
        return new ResponseEntity<>(
                service.getHabitsForDay(LocalDate.now(), Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity<List<Habit>> getAllHabits(@RequestHeader String id) {
        return new ResponseEntity<>(
                service.getAllHabits(Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
