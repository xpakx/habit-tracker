package io.github.xpakx.habitcity.crafting;

import io.github.xpakx.habitcity.crafting.dto.CraftRequest;
import io.github.xpakx.habitcity.shop.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService service;
    
    @PostMapping("/craft")
    public ResponseEntity<ItemResponse> buy(@RequestBody CraftRequest request, @RequestHeader String id) {
        return new ResponseEntity<>(
                service.craft(request, Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
