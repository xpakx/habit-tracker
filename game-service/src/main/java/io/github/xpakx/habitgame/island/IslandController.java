package io.github.xpakx.habitgame.island;

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
                service.getAllIslands(),
                HttpStatus.OK
        );
    }
}
