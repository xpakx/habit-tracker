package io.github.xpakx.habitcity.crafting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query("""
            select r from Recipe r
            where (?1 IS NULL or r.resource1.id = ?1)
            and (?2 IS NULL or r.resource2.id = ?2)
            and (?3 IS NULL or r.resource3.id = ?3)
            and (?4 IS NULL or r.resource4.id = ?4)
            and (?5 IS NULL or r.resource5.id = ?5)
            and (?6 IS NULL or r.resource6.id = ?6)
            and (?7 IS NULL or r.resource7.id = ?7)
            and (?8 IS NULL or r.resource8.id = ?8)
            and (?9 IS NULL or r.resource9.id = ?9)
            """)
    Optional<Recipe> getRecipeByResources(@Nullable Long id, @Nullable Long id1, @Nullable Long id2, @Nullable Long id3, @Nullable Long id4, @Nullable Long id5, @Nullable Long id6, @Nullable Long id7, @Nullable Long id8);
}