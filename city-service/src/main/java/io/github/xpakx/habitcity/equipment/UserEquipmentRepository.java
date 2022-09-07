package io.github.xpakx.habitcity.equipment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEquipmentRepository extends JpaRepository<UserEquipment, Long> {
    Optional<UserEquipment> getByUserId(Long userId);
}