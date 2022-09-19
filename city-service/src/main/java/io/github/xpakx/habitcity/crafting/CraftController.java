package io.github.xpakx.habitcity.crafting;

import io.github.xpakx.habitcity.crafting.dto.CraftRequest;
import io.github.xpakx.habitcity.shop.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CraftController {
    private final CraftService service;

    @PostMapping("/craft")
    public ResponseEntity<ItemResponse> craft(@RequestBody CraftRequest request, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.craft(request, Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
