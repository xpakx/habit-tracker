package io.github.xpakx.habitcity.shop.dto;

import org.springframework.beans.factory.annotation.Value;

public interface ShopEntrySummary {
    Long getId();
    Integer getAmount();
    Integer getPrice();

    @Value("#{target.building != null ? target.building.name : " +
            "(target.ship != null ? target.ship.name : " +
            "(target.resource != null ? target.resource.name : ''))}")
    String getName();
}
