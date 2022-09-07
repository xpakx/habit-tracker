package io.github.xpakx.habitcity.shop.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ShopItemEmptyException extends RuntimeException {
    public ShopItemEmptyException(String message) {
        super(message);
    }
    public ShopItemEmptyException() {
        super("Item sold!");
    }
}