package io.github.xpakx.habitcity.money;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoneyRepository extends JpaRepository<Money, Long> {
    Optional<Money> findByUserId(Long userId);
}