package com.thabelo.smartpantry.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// All the database actions for pantry items (the CRUD).
@Dao
public interface PantryDao {

    // Create: add a new item
    @Insert
    long insert(PantryItem item);

    // Read: get all items, A to Z. LiveData updates the screen by itself.
    @Query("SELECT * FROM pantry_item ORDER BY name COLLATE NOCASE")
    LiveData<List<PantryItem>> getAllLive();

    // Read: get all items once. The matching logic uses this.
    @Query("SELECT * FROM pantry_item")
    List<PantryItem> getAll();

    // Read: get one item by its id (for the edit screen)
    @Query("SELECT * FROM pantry_item WHERE id = :id")
    PantryItem getById(int id);

    // Update: save changes to an item
    @Update
    void update(PantryItem item);

    // Delete: remove an item
    @Delete
    void delete(PantryItem item);
}