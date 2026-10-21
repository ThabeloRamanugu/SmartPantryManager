package com.thabelo.smartpantry.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// One recipe. Becomes the recipe table. Ingredients are kept in a separate table.
@Entity(tableName = "recipe")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;         // recipe name
    public String description;  // short summary
    public int prepMinutes;     // how long it takes
    public String steps;        // each step on a new line
}