package io.github.xpakx.habitcity.shop.dto;

import io.github.xpakx.habitcity.shop.ShopEntry;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShopResponse {
    List<ShopEntry> shopEntries;
}
