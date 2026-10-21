package com.thabelo.smartpantry;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thabelo.smartpantry.ui.PantryFragment;
import com.thabelo.smartpantry.ui.SettingsFragment;
import com.thabelo.smartpantry.ui.SuggestedFragment;

// My main screen. It holds the bottom bar and swaps the fragments.
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);

        // when I tap a tab, I show the matching fragment
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                show(new PantryFragment());
            } else if (id == R.id.nav_recipes) {
                show(new SuggestedFragment());
            } else if (id == R.id.nav_settings) {
                show(new SettingsFragment());
            }
            return true;
        });

        // only on a fresh start I open the Pantry tab,
        // so a screen rotation doesn't reset what I was looking at
        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_pantry);
        }
    }

    // I swap whatever is in the container for the new fragment
    private void show(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}