package io.github.xpakx.habittracker.habit;

import io.github.xpakx.habittracker.habit.dto.CompletionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HabitCompletionController {
    private final HabitCompletionService service;

    @PostMapping("/habit/{habitId}/completion")
    public ResponseEntity<HabitCompletion> completeHabit(@RequestBody CompletionRequest request, @PathVariable Long habitId) {
        return new ResponseEntity<>(
                service.completeHabit(habitId, request),
                HttpStatus.CREATED
        );
    }
}
