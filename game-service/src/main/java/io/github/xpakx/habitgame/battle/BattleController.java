package io.github.xpakx.habitgame.battle;

import io.github.xpakx.habitgame.battle.dto.BattleResponse;
import io.github.xpakx.habitgame.battle.dto.MoveRequest;
import io.github.xpakx.habitgame.battle.dto.MoveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BattleController {
    private final BattleService service;

    @GetMapping("/expedition/{expeditionId}/battle")
    public ResponseEntity<BattleResponse> startBattle(@RequestHeader String id, @PathVariable Long expeditionId) {
        return new ResponseEntity<>(
                service.start(expeditionId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @PostMapping("/battle/{battleId}/position")
    public ResponseEntity<MoveResponse> prepare(@RequestBody MoveRequest request, @RequestHeader String id, @PathVariable Long expeditionId) {
        return new ResponseEntity<>(
                service.prepare(request, expeditionId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @PostMapping("/battle/{battleId}/move")
    public ResponseEntity<MoveResponse> move(@RequestBody MoveRequest request, @RequestHeader String id, @PathVariable Long expeditionId) {
        return new ResponseEntity<>(
                service.move(request, expeditionId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
