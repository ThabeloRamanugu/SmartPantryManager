package com.thabelo.smartpantry;

import android.app.Application;

import com.thabelo.smartpantry.data.RecipeSeeder;

// I use this class to run code once, when my app starts.
public class SmartPantryApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // I add my 20 recipes here. If they already exist, nothing happens.
        RecipeSeeder.seedIfEmpty(this);
    }
}
