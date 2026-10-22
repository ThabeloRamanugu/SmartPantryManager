package com.thabelo.smartpantry.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thabelo.smartpantry.R;
import com.thabelo.smartpantry.data.AppDatabase;
import com.thabelo.smartpantry.data.PantryItem;
import com.thabelo.smartpantry.data.Recipe;
import com.thabelo.smartpantry.data.RecipeDao;
import com.thabelo.smartpantry.data.RecipeIngredient;
import com.thabelo.smartpantry.util.RecipeMatcher;

import java.util.List;

// My Recipe Detail screen. The Intent gives me the recipe id.
public class RecipeDetailActivity extends AppCompatActivity {

    // the key I use to pass the recipe id in the Intent
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    private TextView header, textDesc, textTime, textIngredients, textSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        header = findViewById(R.id.header);
        textDesc = findViewById(R.id.text_desc);
        textTime = findViewById(R.id.text_time);
        textIngredients = findViewById(R.id.text_ingredients);
        textSteps = findViewById(R.id.text_steps);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // I push the header below the status bar
        int basePad = header.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(header, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), basePad + bars.top,
                    v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        if (recipeId == -1) {
            finish();
            return;
        }
        loadRecipe(recipeId);
    }

    // I load the recipe and the pantry on my background thread
    private void loadRecipe(int recipeId) {
        AppDatabase.dbExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            RecipeDao dao = db.recipeDao();
            Recipe recipe = dao.getRecipeById(recipeId);
            List<RecipeIngredient> needed = dao.getIngredientsForRecipe(recipeId);
            List<PantryItem> pantry = db.pantryDao().getAll();

            // I reuse the matcher so the detail screen agrees with the suggestions
            RecipeMatcher.Result result = RecipeMatcher.check(recipe, needed, pantry);

            runOnUiThread(() -> show(recipe, needed, result));
        });
    }

    // I put the recipe on the screen
    private void show(Recipe recipe, List<RecipeIngredient> needed, RecipeMatcher.Result result) {
        if (recipe == null) {
            finish();
            return;
        }

        header.setText(recipe.name);
        textDesc.setText(recipe.description);
        textTime.setText(getString(R.string.prep_time, recipe.prepMinutes));

        // one line per ingredient, the missing ones are marked in red
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (RecipeIngredient i : needed) {
            String qty = (i.quantity == Math.floor(i.quantity))
                    ? String.valueOf((long) i.quantity)
                    : String.valueOf(i.quantity);
            String line = "\u2022 " + qty + " " + i.unit + "  " + i.name;

            int start = builder.length();
            builder.append(line);

            if (result.missing.contains(i.name)) {
                builder.append("  ").append(getString(R.string.missing_tag));
                builder.setSpan(new ForegroundColorSpan(Color.parseColor("#C62828")),
                        start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            builder.append("\n");
        }
        textIngredients.setText(builder);

        // I number the method steps, the steps are split by new lines
        StringBuilder steps = new StringBuilder();
        String[] parts = recipe.steps.split("\n");
        for (int n = 0; n < parts.length; n++) {
            steps.append(n + 1).append(". ").append(parts[n]);
            if (n < parts.length - 1) steps.append("\n");
        }
        textSteps.setText(steps.toString());
    }
}