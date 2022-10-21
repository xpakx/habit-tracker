package io.github.xpakx.habitcity.city;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByIdAndUserId(Long cityId, Long userId);

    List<City> findByUserId(Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}