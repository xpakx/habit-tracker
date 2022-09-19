package io.github.xpakx.habitcity.city;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByIdAndUserId(Long cityId, Long userId);
}