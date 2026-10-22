package com.thabelo.smartpantry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.thabelo.smartpantry.data.PantryItem;
import com.thabelo.smartpantry.data.Recipe;
import com.thabelo.smartpantry.data.RecipeIngredient;
import com.thabelo.smartpantry.util.RecipeMatcher;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// My tests for the strict-matching rule.
public class RecipeMatcherTest {

    // small helper: makes a pantry item
    private PantryItem have(String name, double qty, String unit) {
        PantryItem p = new PantryItem();
        p.name = name;
        p.quantity = qty;
        p.unit = unit;
        return p;
    }

    // small helper: makes a recipe ingredient
    private RecipeIngredient need(String name, double qty, String unit) {
        RecipeIngredient i = new RecipeIngredient();
        i.name = name;
        i.quantity = qty;
        i.unit = unit;
        return i;
    }

    // small helper: runs the matcher on a pantry and a list of needed ingredients
    private RecipeMatcher.Result run(List<PantryItem> pantry, RecipeIngredient... needed) {
        return RecipeMatcher.check(new Recipe(), Arrays.asList(needed), pantry);
    }

    // everything is there, so the recipe is suggested
    @Test
    public void allIngredientsPresent_isSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                have("Egg", 3, "piece"), have("Bread", 2, "piece"), have("Butter", 10, "g"));
        RecipeMatcher.Result r = run(pantry,
                need("Egg", 3, "piece"), need("Bread", 2, "piece"), need("Butter", 10, "g"));
        assertTrue(r.isStrictMatch());
    }

    // 4 of 5 ingredients means NOT suggested, but it is "almost there"
    @Test
    public void fourOfFiveIngredients_isNotSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                have("Pasta", 200, "g"), have("Tomato", 4, "piece"),
                have("Garlic", 2, "piece"), have("Olive oil", 2, "tbsp"));
        RecipeMatcher.Result r = run(pantry,
                need("Pasta", 200, "g"), need("Tomato", 4, "piece"), need("Garlic", 2, "piece"),
                need("Olive oil", 2, "tbsp"), need("Salt", 1, "tsp"));
        assertFalse(r.isStrictMatch());
        assertEquals(1, r.missing.size());
        assertTrue(r.isAlmostThere());
    }

    // capitals, spaces and plurals should not break matching
    @Test
    public void pluralAndCapitals_stillMatch() {
        List<PantryItem> pantry = Arrays.asList(have("  TOMATOES ", 4, "piece"));
        assertTrue(run(pantry, need("Tomato", 4, "piece")).isStrictMatch());
    }

    // not enough of an ingredient means it is missing
    @Test
    public void notEnoughQuantity_isNotSuggested() {
        List<PantryItem> pantry = Arrays.asList(have("Flour", 100, "g"));
        assertFalse(run(pantry, need("Flour", 200, "g")).isStrictMatch());
    }

    // 1 kg is enough for a recipe that needs 200 g
    @Test
    public void kilogramsCoverGrams() {
        List<PantryItem> pantry = Arrays.asList(have("Flour", 1, "kg"));
        assertTrue(run(pantry, need("Flour", 200, "g")).isStrictMatch());
    }

    // 30 ml is the same as 2 tablespoons
    @Test
    public void tablespoonsAndMillilitresConvert() {
        List<PantryItem> pantry = Arrays.asList(have("Olive oil", 30, "ml"));
        assertTrue(run(pantry, need("Olive oil", 2, "tbsp")).isStrictMatch());
    }

    // two entries of the same ingredient are added together
    @Test
    public void twoEntriesOfSameIngredient_areAddedTogether() {
        List<PantryItem> pantry = Arrays.asList(have("Egg", 2, "piece"), have("Eggs", 1, "piece"));
        assertTrue(run(pantry, need("Egg", 3, "piece")).isStrictMatch());
    }

    // grams cannot stand in for pieces
    @Test
    public void differentKindOfUnit_isNotSwapped() {
        List<PantryItem> pantry = Arrays.asList(have("Garlic", 10, "g"));
        assertFalse(run(pantry, need("Garlic", 2, "piece")).isStrictMatch());
    }

    // an empty pantry matches nothing
    @Test
    public void emptyPantry_matchesNothing() {
        List<PantryItem> pantry = new ArrayList<>();
        assertFalse(run(pantry, need("Egg", 1, "piece")).isStrictMatch());
    }
}