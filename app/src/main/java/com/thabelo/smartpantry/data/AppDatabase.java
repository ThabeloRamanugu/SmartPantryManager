package com.thabelo.smartpantry.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// The main database. Only one copy exists in the whole app.
@Database(
        entities = {PantryItem.class, Recipe.class, RecipeIngredient.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PantryDao pantryDao();
    public abstract RecipeDao recipeDao();

    private static volatile AppDatabase instance;

    // Room does not allow database work on the main thread.
    // So we run it on this background thread instead.
    public static final ExecutorService dbExecutor = Executors.newFixedThreadPool(2);

    // Gives back the one database. Creates it the first time only.
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "smart_pantry.db").build();
                }
            }
        }
        return instance;
    }
}
