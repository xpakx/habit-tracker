package io.github.xpakx.habitcity.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    @Query(nativeQuery = true, value = "select r from resource r WHERE r.rarity <= :rarity ORDER BY RAND() LIMIT :amount")
    public List<Resource> findRandomResources(int amount, int rarity);
}