package io.github.xpakx.habitcity.shop;

import io.github.xpakx.habitcity.shop.dto.ShopEntrySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopEntryRepository extends JpaRepository<ShopEntry, Long> {
    List<ShopEntrySummary> findByShopId(Long shopId);
}