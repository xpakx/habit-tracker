package io.github.xpakx.habitcity.crafting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query("""
            select r from Recipe r
            where r.resource1.id = ?1 and r.resource2.id = ?2 and r.resource3.id = ?3 and r.resource4.id = ?4 and r.resource5.id = ?5 and r.resource6.id = ?6 and r.resource7.id = ?7 and r.resource8.id = ?8 and r.resource9.id = ?9""")
    Optional<Recipe> getRecipeByResources(Long id, Long id1, Long id2, Long id3, Long id4, Long id5, Long id6, Long id7, Long id8);
    
}