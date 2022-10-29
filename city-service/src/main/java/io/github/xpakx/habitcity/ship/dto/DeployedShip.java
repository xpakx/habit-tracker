package io.github.xpakx.habitcity.ship.dto;

import org.springframework.beans.factory.annotation.Value;

public interface DeployedShip {
    Long getId();
    @Value("#{target.ship.name}")
    String getName();
    @Value("#{target.ship.id}")
    Long getShipId();
    @Value("#{target.ship.code}")
    String getCode();
    boolean getBlocked();
}
