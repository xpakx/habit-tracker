package io.github.xpakx.habitcity.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShopResponse {
    List<ShopEntrySummary> items;
}
