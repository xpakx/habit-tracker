package io.github.xpakx.habitgamification.gamification;

import io.github.xpakx.habitgamification.gamification.dto.ExpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GamificationController {
    private final GamificationService service;
    @GetMapping("/experience")
    public ResponseEntity<ExpResponse> getExperience(@RequestHeader String id) {
        return new ResponseEntity<>(
                service.getExp(Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
