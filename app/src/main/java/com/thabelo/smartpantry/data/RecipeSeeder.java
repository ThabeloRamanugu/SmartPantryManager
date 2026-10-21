package com.thabelo.smartpantry.data;

import android.content.Context;

import com.thabelo.smartpantry.util.IngredientNormalizer;

import java.util.ArrayList;
import java.util.List;

// I use this class to add my 20 starter recipes the first time the app runs.
public final class RecipeSeeder {

    // I made this private because I only use static methods, so I never need an object.
    private RecipeSeeder() { }

    // I call this when the app starts. If the recipes are already saved, it does nothing.
    public static void seedIfEmpty(Context context) {
        // Room won't let me use the database on the main thread, so I use my background thread
        AppDatabase.dbExecutor.execute(() -> {
            RecipeDao dao = AppDatabase.getInstance(context).recipeDao();

            // if I already have recipes, I stop here so I never add them twice
            if (dao.countRecipes() > 0) return;

            add(dao, "Scrambled Eggs on Toast", "Quick, protein-rich breakfast.", 10,
                    "Whisk the eggs with salt.\nMelt butter in a pan on low heat.\nStir eggs gently until just set.\nServe on toasted bread.",
                    ing("Egg", 3, "piece"), ing("Bread", 2, "piece"),
                    ing("Butter", 10, "g"), ing("Salt", 1, "tsp"));

            add(dao, "Simple Tomato Pasta", "Classic pasta with a fresh tomato sauce.", 25,
                    "Boil the pasta in salted water.\nFry garlic in olive oil for 1 minute.\nAdd chopped tomatoes and simmer 10 minutes.\nToss the pasta in the sauce.",
                    ing("Pasta", 200, "g"), ing("Tomato", 4, "piece"), ing("Garlic", 2, "piece"),
                    ing("Olive oil", 2, "tbsp"), ing("Salt", 1, "tsp"));

            add(dao, "Veggie Omelette", "Fluffy omelette packed with leftover veg.", 15,
                    "Dice the onion and pepper and fry until soft.\nBeat the eggs and pour over.\nAdd grated cheese and fold when set.",
                    ing("Egg", 3, "piece"), ing("Onion", 1, "piece"), ing("Bell pepper", 1, "piece"),
                    ing("Cheese", 50, "g"), ing("Butter", 10, "g"));

            add(dao, "Fluffy Pancakes", "Sweet breakfast pancakes from pantry basics.", 20,
                    "Mix flour, sugar, milk and eggs into a smooth batter.\nMelt butter in a pan.\nCook spoonfuls until bubbles form, flip and cook 1 minute.",
                    ing("Flour", 200, "g"), ing("Milk", 300, "ml"), ing("Egg", 2, "piece"),
                    ing("Sugar", 2, "tbsp"), ing("Butter", 20, "g"));

            add(dao, "Egg Fried Rice", "A great way to use leftover rice and veg.", 20,
                    "Heat oil and scramble the eggs, then set aside.\nFry carrot and peas for 3 minutes.\nAdd rice and soy sauce and stir-fry.\nMix the eggs back in.",
                    ing("Rice", 200, "g"), ing("Egg", 2, "piece"), ing("Carrot", 1, "piece"),
                    ing("Peas", 80, "g"), ing("Soy sauce", 2, "tbsp"), ing("Vegetable oil", 1, "tbsp"));

            add(dao, "Creamy Mashed Potatoes", "Comfort-food side dish.", 30,
                    "Peel and boil the potatoes until soft.\nDrain and mash with butter and warm milk.\nSeason with salt.",
                    ing("Potato", 4, "piece"), ing("Butter", 30, "g"),
                    ing("Milk", 100, "ml"), ing("Salt", 1, "tsp"));

            add(dao, "Cheese Toastie", "Crispy, gooey toasted sandwich.", 10,
                    "Butter the outside of the bread.\nPlace cheese between the slices.\nToast in a pan until golden on both sides.",
                    ing("Bread", 2, "piece"), ing("Cheese", 60, "g"), ing("Butter", 10, "g"));

            add(dao, "Chicken Stir-Fry", "Fast weeknight dinner.", 25,
                    "Slice the chicken, pepper and onion.\nFry chicken in oil until cooked through.\nAdd veg and garlic, then soy sauce.\nCook 4 more minutes.",
                    ing("Chicken breast", 300, "g"), ing("Bell pepper", 1, "piece"), ing("Onion", 1, "piece"),
                    ing("Soy sauce", 2, "tbsp"), ing("Vegetable oil", 1, "tbsp"), ing("Garlic", 2, "piece"));

            add(dao, "Tuna Pasta Salad", "Cold salad that keeps well for lunch.", 20,
                    "Boil and cool the pasta.\nChop the onion finely.\nMix everything with the mayonnaise.",
                    ing("Pasta", 150, "g"), ing("Tuna", 150, "g"),
                    ing("Mayonnaise", 2, "tbsp"), ing("Onion", 1, "piece"));

            add(dao, "Hearty Lentil Soup", "Filling, cheap and healthy.", 40,
                    "Fry onion, carrot and garlic in oil.\nAdd lentils and stock.\nSimmer 25 minutes until soft.",
                    ing("Lentil", 200, "g"), ing("Onion", 1, "piece"), ing("Carrot", 2, "piece"),
                    ing("Garlic", 2, "piece"), ing("Vegetable stock", 500, "ml"), ing("Olive oil", 1, "tbsp"));

            add(dao, "Banana Oat Porridge", "Warm and sweet breakfast bowl.", 10,
                    "Heat oats and milk, stirring, for 5 minutes.\nSlice the banana on top.\nDrizzle with honey.",
                    ing("Oat", 80, "g"), ing("Milk", 250, "ml"), ing("Banana", 1, "piece"), ing("Honey", 1, "tbsp"));

            add(dao, "Avocado Toast", "Simple, fresh and ready in minutes.", 5,
                    "Toast the bread.\nMash the avocado with lemon juice and salt.\nSpread over the toast.",
                    ing("Avocado", 1, "piece"), ing("Bread", 2, "piece"),
                    ing("Lemon", 1, "piece"), ing("Salt", 1, "tsp"));

            add(dao, "Crispy Potato Wedges", "Oven-baked wedges with paprika.", 40,
                    "Heat oven to 220 C.\nCut potatoes into wedges and toss with oil, paprika and salt.\nBake 30 minutes, turning once.",
                    ing("Potato", 3, "piece"), ing("Olive oil", 2, "tbsp"),
                    ing("Paprika", 1, "tsp"), ing("Salt", 1, "tsp"));

            add(dao, "Spaghetti Aglio e Olio", "Garlic, oil and chilli: Italian simplicity.", 20,
                    "Boil the spaghetti.\nGently fry sliced garlic and chilli flakes in oil.\nToss the spaghetti in the oil.",
                    ing("Spaghetti", 200, "g"), ing("Garlic", 4, "piece"),
                    ing("Olive oil", 4, "tbsp"), ing("Chilli flakes", 1, "tsp"));

            add(dao, "Creamy Mushroom Rice", "Rich one-pan rice dish.", 35,
                    "Fry onion and mushrooms in butter.\nAdd rice and cook 2 minutes.\nPour in cream and 200 ml water, cover and simmer 15 minutes.",
                    ing("Mushroom", 200, "g"), ing("Rice", 150, "g"), ing("Cream", 100, "ml"),
                    ing("Onion", 1, "piece"), ing("Butter", 20, "g"));

            add(dao, "Chickpea Curry", "Easy vegan curry.", 30,
                    "Fry onion and garlic.\nAdd curry powder, tomatoes and chickpeas.\nPour in coconut milk and simmer 15 minutes.",
                    ing("Chickpea", 400, "g"), ing("Tomato", 2, "piece"), ing("Onion", 1, "piece"),
                    ing("Garlic", 2, "piece"), ing("Curry powder", 2, "tsp"), ing("Coconut milk", 200, "ml"));

            add(dao, "French Toast", "Eggy bread, sweet and golden.", 15,
                    "Whisk eggs, milk and sugar.\nSoak the bread slices.\nFry in butter until golden on both sides.",
                    ing("Bread", 4, "piece"), ing("Egg", 2, "piece"), ing("Milk", 100, "ml"),
                    ing("Sugar", 1, "tbsp"), ing("Butter", 10, "g"));

            add(dao, "Greek-Style Salad", "Fresh, crunchy and no cooking needed.", 10,
                    "Chop cucumber, tomatoes and onion.\nCrumble feta over the top.\nDress with olive oil.",
                    ing("Cucumber", 1, "piece"), ing("Tomato", 3, "piece"), ing("Feta cheese", 100, "g"),
                    ing("Olive oil", 2, "tbsp"), ing("Onion", 1, "piece"));

            add(dao, "Beans on Toast", "Cheap, classic comfort food.", 10,
                    "Warm the beans in a pot.\nToast and butter the bread.\nTop with beans and grated cheese.",
                    ing("Baked bean", 400, "g"), ing("Bread", 2, "piece"),
                    ing("Butter", 10, "g"), ing("Cheese", 30, "g"));

            add(dao, "Simple Vegetable Soup", "Uses up leftover veg.", 35,
                    "Chop the potatoes, carrots and onion.\nSimmer in the stock for 25 minutes.\nSeason with salt and blend if you like.",
                    ing("Potato", 2, "piece"), ing("Carrot", 2, "piece"), ing("Onion", 1, "piece"),
                    ing("Vegetable stock", 500, "ml"), ing("Salt", 1, "tsp"));
        });
    }

    // I save one recipe first, then save all of its ingredient lines.
    private static void add(RecipeDao dao, String name, String description, int minutes,
                            String steps, RecipeIngredient... ingredients) {
        // I fill in the recipe details
        Recipe r = new Recipe();
        r.name = name;
        r.description = description;
        r.prepMinutes = minutes;
        r.steps = steps;

        // insert gives me back the new recipe id, and I need it for the ingredients
        long recipeId = dao.insertRecipe(r);

        // I link every ingredient to this recipe using that id
        List<RecipeIngredient> list = new ArrayList<>();
        for (RecipeIngredient i : ingredients) {
            i.recipeId = (int) recipeId;
            list.add(i);
        }

        // then I save all the ingredient lines in one go
        dao.insertIngredients(list);
    }

    // I build one ingredient line and also save its cleaned name, which I use for matching.
    private static RecipeIngredient ing(String name, double qty, String unit) {
        RecipeIngredient i = new RecipeIngredient();
        i.name = name;
        i.normalizedName = IngredientNormalizer.normalize(name);
        i.quantity = qty;
        i.unit = unit;
        return i;
    }
}