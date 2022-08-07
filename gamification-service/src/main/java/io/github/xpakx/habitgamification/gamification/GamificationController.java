package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.gamification.dto.CompletionResult;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GamificationController {
    private final GamificationService service;

    @PostMapping("/attempt")
    public ResponseEntity<CompletionResult> newAttempt(@RequestBody HabitCompletion request) {
        return new ResponseEntity<>(
                service.newAttempt(request),
                HttpStatus.OK
        );
    }
}
