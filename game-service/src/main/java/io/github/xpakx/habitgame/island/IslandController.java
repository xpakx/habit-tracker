package io.github.xpakx.habitgame.island;

import io.github.xpakx.habitgame.island.dto.NamingIslandRequest;
import io.github.xpakx.habitgame.island.dto.NamingIslandResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class IslandController {
    private final IslandService service;

    @GetMapping("/island/all")
    public ResponseEntity<List<Island>> getAllIslands(@RequestHeader String id) {
        return new ResponseEntity<>(
                service.getAllIslands(Long.valueOf(id)),
                HttpStatus.OK
        );
    }

    @PostMapping("/island/{islandId}/name")
    public ResponseEntity<NamingIslandResponse> nameIsland(@RequestHeader String id, @PathVariable Long islandId, @RequestBody NamingIslandRequest request) {
        return new ResponseEntity<>(
                service.nameIsland(request, islandId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
