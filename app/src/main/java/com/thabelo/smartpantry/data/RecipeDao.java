package com.thabelo.smartpantry.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// Database actions for recipes and their ingredients.
@Dao
public interface RecipeDao {

    // Add one recipe. Returns its new id.
    @Insert
    long insertRecipe(Recipe recipe);

    // Add many ingredient lines at once
    @Insert
    void insertIngredients(List<RecipeIngredient> ingredients);

    // Get all recipes, A to Z
    @Query("SELECT * FROM recipe ORDER BY name")
    List<Recipe> getAllRecipes();

    // Get one recipe by id
    @Query("SELECT * FROM recipe WHERE id = :id")
    Recipe getRecipeById(int id);

    // Get all ingredient lines for one recipe
    @Query("SELECT * FROM recipe_ingredient WHERE recipeId = :recipeId")
    List<RecipeIngredient> getIngredientsForRecipe(int recipeId);

    // How many recipes exist. Used to check if we already added them.
    @Query("SELECT COUNT(*) FROM recipe")
    int countRecipes();
}