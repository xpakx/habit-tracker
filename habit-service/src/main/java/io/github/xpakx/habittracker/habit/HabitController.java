package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.HabitRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
