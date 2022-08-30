package io.github.xpakx.habitcity.money;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyRepository extends JpaRepository<Money, Long> {
}