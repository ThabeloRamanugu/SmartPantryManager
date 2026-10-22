package com.thabelo.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thabelo.smartpantry.R;
import com.thabelo.smartpantry.data.Recipe;
import com.thabelo.smartpantry.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

// My adapter for the suggestions list. It shows three kinds of rows:
// section titles, recipe cards and a message.
public class SuggestedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // I use this to tell the fragment which recipe was tapped
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private static final int TYPE_SECTION = 0;
    private static final int TYPE_RECIPE = 1;
    private static final int TYPE_MESSAGE = 2;

    // One row in the list. Only the fields for its type are used.
    public static class Row {
        final int type;
        final String text;
        final RecipeMatcher.Result result;
        final boolean almost;

        private Row(int type, String text, RecipeMatcher.Result result, boolean almost) {
            this.type = type;
            this.text = text;
            this.result = result;
            this.almost = almost;
        }

        public static Row section(String text) {
            return new Row(TYPE_SECTION, text, null, false);
        }

        public static Row message(String text) {
            return new Row(TYPE_MESSAGE, text, null, false);
        }

        public static Row recipe(RecipeMatcher.Result result, boolean almost) {
            return new Row(TYPE_RECIPE, null, result, almost);
        }
    }

    private List<Row> rows = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public SuggestedAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    // I call this with the new rows after the matching is done
    public void setRows(List<Row> newRows) {
        this.rows = newRows;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_RECIPE) {
            return new RecipeHolder(inflater.inflate(R.layout.item_recipe, parent, false));
        }
        return new TextHolder(inflater.inflate(R.layout.item_section, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Row row = rows.get(position);

        if (row.type == TYPE_RECIPE) {
            RecipeHolder h = (RecipeHolder) holder;
            Recipe recipe = row.result.recipe;
            h.title.setText(recipe.name);
            h.desc.setText(recipe.description);

            // almost-there cards say what is missing, ready cards show the time
            if (row.almost) {
                h.meta.setText(h.itemView.getContext()
                        .getString(R.string.missing_one, row.result.missing.get(0)));
                h.meta.setTextColor(android.graphics.Color.parseColor("#E65100"));
            } else {
                h.meta.setText(h.itemView.getContext()
                        .getString(R.string.prep_time, recipe.prepMinutes));
                h.meta.setTextColor(android.graphics.Color.parseColor("#6B776B"));
            }
            h.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
        } else {
            TextHolder h = (TextHolder) holder;
            h.text.setText(row.text);

            // section titles are bold and green, the message is plain grey
            boolean isSection = row.type == TYPE_SECTION;
            h.text.setTextSize(isSection ? 18 : 15);
            h.text.setTypeface(null, isSection
                    ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
            h.text.setTextColor(android.graphics.Color.parseColor(
                    isSection ? "#1B5E20" : "#6B776B"));
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    // holds the views of a section title or message row
    static class TextHolder extends RecyclerView.ViewHolder {
        final TextView text;

        TextHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text_section);
        }
    }

    // holds the views of a recipe card
    static class RecipeHolder extends RecyclerView.ViewHolder {
        final TextView title, desc, meta;

        RecipeHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title);
            desc = itemView.findViewById(R.id.text_desc);
            meta = itemView.findViewById(R.id.text_meta);
        }
    }
}