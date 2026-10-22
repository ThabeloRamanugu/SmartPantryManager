package com.thabelo.smartpantry.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.thabelo.smartpantry.data.AppDatabase;
import com.thabelo.smartpantry.data.PantryItem;

import java.util.List;

// I use this to hold the pantry list for the screen.
// It survives a screen rotation, so the list does not reload every time.
public class PantryViewModel extends AndroidViewModel {

    private final AppDatabase db;

    // LiveData tells my screen by itself when the table changes
    private final LiveData<List<PantryItem>> items;

    public PantryViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        items = db.pantryDao().getAllLive();
    }

    public LiveData<List<PantryItem>> getItems() {
        return items;
    }

    // I delete on my background thread because Room blocks the main thread
    public void delete(PantryItem item) {
        AppDatabase.dbExecutor.execute(() -> db.pantryDao().delete(item));
    }

    // I use this for the undo button after a swipe delete
    public void restore(PantryItem item) {
        AppDatabase.dbExecutor.execute(() -> db.pantryDao().insert(item));
    }
}