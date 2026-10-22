package com.thabelo.smartpantry.util;

import com.thabelo.smartpantry.data.PantryItem;
import com.thabelo.smartpantry.data.Recipe;
import com.thabelo.smartpantry.data.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// This is my strict-matching logic. A recipe only counts if the pantry
// has EVERY ingredient in at least the amount the recipe needs.
public final class RecipeMatcher {

    // tiny gap so decimal rounding does not break the comparison
    private static final double EPS = 0.0001;

    // The answer for one recipe: what is missing, if anything.
    public static class Result {
        public final Recipe recipe;
        public final List<RecipeIngredient> ingredients;
        public final List<String> missing;

        Result(Recipe recipe, List<RecipeIngredient> ingredients, List<String> missing) {
            this.recipe = recipe;
            this.ingredients = ingredients;
            this.missing = missing;
        }

        // strict match: nothing is missing (and the recipe has ingredients)
        public boolean isStrictMatch() {
            return !ingredients.isEmpty() && missing.isEmpty();
        }

        // bonus list: only one ingredient is missing
        public boolean isAlmostThere() {
            return missing.size() == 1;
        }
    }

    // Nobody makes an object of this class, I only use the static methods.
    private RecipeMatcher() { }

    // I check one recipe against the whole pantry.
    public static Result check(Recipe recipe, List<RecipeIngredient> ingredients,
                               List<PantryItem> pantry) {
        Map<String, Double> have = totalsFromPantry(pantry);
        List<String> missing = new ArrayList<>();

        for (RecipeIngredient need : ingredients) {
            String key = key(need.name, need.unit);
            double owned = have.containsKey(key) ? have.get(key) : 0;
            double required = need.quantity * factor(need.unit);

            // not enough of it (or none at all) means it is missing
            if (owned + EPS < required) {
                missing.add(need.name);
            }
        }
        return new Result(recipe, ingredients, missing);
    }

    // I add up everything in the pantry, in base units (g, ml or piece).
    // Two entries of the same ingredient are added together.
    private static Map<String, Double> totalsFromPantry(List<PantryItem> pantry) {
        Map<String, Double> totals = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = key(item.name, item.unit);
            double amount = item.quantity * factor(item.unit);
            double old = totals.containsKey(key) ? totals.get(key) : 0;
            totals.put(key, old + amount);
        }
        return totals;
    }

    // The key is the cleaned name plus the kind of unit.
    // "Tomatoes" + "piece" becomes "tomato|count".
    private static String key(String name, String unit) {
        return IngredientNormalizer.normalize(name) + "|" + kind(unit);
    }

    // What kind of thing the unit measures. Different kinds can't be swapped.
    private static String kind(String unit) {
        switch (clean(unit)) {
            case "g":
            case "kg":
                return "weight";
            case "ml":
            case "l":
            case "tsp":
            case "tbsp":
                return "volume";
            default:
                return "count";
        }
    }

    // How many base units (g, ml, piece) one of this unit is.
    private static double factor(String unit) {
        switch (clean(unit)) {
            case "kg":
            case "l":
                return 1000;
            case "tbsp":
                return 15;   // 1 tablespoon is about 15 ml
            case "tsp":
                return 5;    // 1 teaspoon is about 5 ml
            default:
                return 1;    // g, ml and piece
        }
    }

    private static String clean(String unit) {
        return unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
    }
}