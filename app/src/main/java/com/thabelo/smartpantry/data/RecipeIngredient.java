package com.thabelo.smartpantry.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// One ingredient line for a recipe. A recipe has many of these.
// If a recipe is deleted, its ingredients go too (CASCADE).
@Entity(
        tableName = "recipe_ingredient",
        foreignKeys = @ForeignKey(
                entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("recipeId")})
public class RecipeIngredient {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int recipeId;          // which recipe this belongs to
    public String name;           // like "Tomato"
    public String normalizedName; // cleaned name for matching
    public double quantity;       // how much the recipe needs
    public String unit;           // g, ml, piece, tsp or tbsp
}