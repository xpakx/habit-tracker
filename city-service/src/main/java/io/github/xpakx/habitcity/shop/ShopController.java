package io.github.xpakx.habitcity.shop;

import io.github.xpakx.habitcity.shop.dto.BuyRequest;
import io.github.xpakx.habitcity.shop.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ShopController {
    private final ShopService service;

    @PostMapping("/shop/item/{entryId}")
    public ResponseEntity<ItemResponse> buy(@RequestBody BuyRequest request, @RequestHeader String id, @PathVariable Long entryId) {
        return new ResponseEntity<>(
                service.buy(request, entryId, Long.valueOf(id)),
                HttpStatus.OK
        );
    }
}
