package io.github.xpakx.habitcity.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopEntryRepository extends JpaRepository<ShopEntry, Long> {
}