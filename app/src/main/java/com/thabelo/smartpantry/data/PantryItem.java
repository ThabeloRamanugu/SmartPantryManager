package com.thabelo.smartpantry.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// One ingredient the user has at home. Becomes the pantry_item table.
@Entity(tableName = "pantry_item")
public class PantryItem {

    @PrimaryKey(autoGenerate = true)
    public int id;                 // made automatically

    public String name;            // what the user typed, like "Tomatoes"
    public String normalizedName;  // cleaned name for matching, like "tomato"
    public double quantity;        // how much they have
    public String unit;            // g, ml, piece, tsp or tbsp
    public Long expiryDate;        // date in milliseconds, null if none
}