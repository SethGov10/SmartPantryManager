package com.example.smartpantrymanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RecipeMatcherTest {

    // Cheese Omelette needs 3 eggs and 50 g cheese
    private Recipe omelette() {
        List<RecipeIngredient> needs = new ArrayList<>();
        needs.add(new RecipeIngredient("egg", 3, "piece"));
        needs.add(new RecipeIngredient("cheese", 50, "g"));
        return new Recipe(1, "Cheese Omelette", "steps", needs);
    }

    @Test
    public void allIngredientsPresent_isSuggested() {
        List<Ingredient> pantry = new ArrayList<>();
        pantry.add(new Ingredient("egg", 6, "piece", ""));
        pantry.add(new Ingredient("cheese", 100, "g", ""));
        assertTrue(RecipeMatcher.canMake(omelette(), pantry));
    }

    @Test
    public void oneIngredientMissing_isNotSuggested() {
        List<Ingredient> pantry = new ArrayList<>();
        pantry.add(new Ingredient("egg", 6, "piece", ""));
        assertFalse(RecipeMatcher.canMake(omelette(), pantry));
    }

    @Test
    public void insufficientQuantity_isNotSuggested() {
        List<Ingredient> pantry = new ArrayList<>();
        pantry.add(new Ingredient("egg", 2, "piece", ""));
        pantry.add(new Ingredient("cheese", 100, "g", ""));
        assertFalse(RecipeMatcher.canMake(omelette(), pantry));
    }

    @Test
    public void pluralAndCaseDifferences_stillMatch() {
        List<Ingredient> pantry = new ArrayList<>();
        pantry.add(new Ingredient("Eggs", 3, "piece", ""));
        pantry.add(new Ingredient("CHEESE", 50, "g", ""));
        assertTrue(RecipeMatcher.canMake(omelette(), pantry));
    }

    @Test
    public void unitDifferences_areConverted() {
        List<Ingredient> pantry = new ArrayList<>();
        pantry.add(new Ingredient("egg", 3, "piece", ""));
        pantry.add(new Ingredient("cheese", 0.1, "kg", "")); // 0.1 kg = 100 g
        assertTrue(RecipeMatcher.canMake(omelette(), pantry));
    }

    @Test
    public void tomatoesMatchesTomato() {
        assertTrue(RecipeMatcher.normalize("Tomatoes").equals(RecipeMatcher.normalize("tomato")));
    }
}