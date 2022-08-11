package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.gamification.dto.CompletionResult;
import io.github.xpakx.habitgamification.gamification.dto.ExpResponse;
import io.github.xpakx.habitgamification.gamification.dto.HabitCompletion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GamificationController {
    private final GamificationService service;
    @GetMapping("/experience/{userId}")
    public ResponseEntity<ExpResponse> getExperience(@PathVariable Long userId) {
        return new ResponseEntity<>(
                service.getExp(userId),
                HttpStatus.OK
        );
    }
}
