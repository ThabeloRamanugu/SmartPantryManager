package com.thabelo.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thabelo.smartpantry.R;
import com.thabelo.smartpantry.data.AppDatabase;
import com.thabelo.smartpantry.data.PantryItem;
import com.thabelo.smartpantry.data.Recipe;
import com.thabelo.smartpantry.data.RecipeDao;
import com.thabelo.smartpantry.data.RecipeIngredient;
import com.thabelo.smartpantry.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

// My Suggested Recipes screen. It runs the strict matching on the current pantry.
public class SuggestedFragment extends Fragment {

    private SuggestedAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggested, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // I push the header below the status bar
        View header = view.findViewById(R.id.header);
        int basePad = header.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(header, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), basePad + bars.top,
                    v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        // tapping a card opens the detail screen and sends the recipe id in an Intent
        adapter = new SuggestedAdapter(recipe -> {
            Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.id);
            startActivity(intent);
        });

        RecyclerView recycler = view.findViewById(R.id.recycler_suggested);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);
    }

    // I match again every time the screen is shown, so it always uses the latest pantry
    @Override
    public void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        // I grab the context now, on the main thread, so the background thread is safe
        final android.content.Context appContext = requireContext().getApplicationContext();

        // database work runs on my background thread
        AppDatabase.dbExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(appContext);
            RecipeDao recipeDao = db.recipeDao();
            List<PantryItem> pantry = db.pantryDao().getAll();

            List<RecipeMatcher.Result> ready = new ArrayList<>();
            List<RecipeMatcher.Result> almost = new ArrayList<>();

            // I check every recipe against the pantry
            for (Recipe recipe : recipeDao.getAllRecipes()) {
                List<RecipeIngredient> needed = recipeDao.getIngredientsForRecipe(recipe.id);
                RecipeMatcher.Result result = RecipeMatcher.check(recipe, needed, pantry);

                if (result.isStrictMatch()) {
                    ready.add(result);
                } else if (result.isAlmostThere()) {
                    almost.add(result);
                }
            }

            // I build the rows on the main thread because they use strings and views
            if (getActivity() == null) return;
            requireActivity().runOnUiThread(() -> showRows(ready, almost));
        });
    }

    // I turn the two lists into rows: strict matches first, "almost there" second
    private void showRows(List<RecipeMatcher.Result> ready, List<RecipeMatcher.Result> almost) {
        if (!isAdded()) return;

        List<SuggestedAdapter.Row> rows = new ArrayList<>();

        rows.add(SuggestedAdapter.Row.section(getString(R.string.section_ready)));
        if (ready.isEmpty()) {
            // the message the brief asks for when nothing matches
            rows.add(SuggestedAdapter.Row.message(getString(R.string.no_match)));
        } else {
            for (RecipeMatcher.Result r : ready) {
                rows.add(SuggestedAdapter.Row.recipe(r, false));
            }
        }

        // the bonus list is only shown when it has something, and it stays separate
        if (!almost.isEmpty()) {
            rows.add(SuggestedAdapter.Row.section(getString(R.string.section_almost)));
            for (RecipeMatcher.Result r : almost) {
                rows.add(SuggestedAdapter.Row.recipe(r, true));
            }
        }

        adapter.setRows(rows);
    }
}