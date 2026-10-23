# Smart Pantry Manager

A Java Android app that helps cut food waste. You list the ingredients you already
have at home, and the app suggests only the recipes you can cook right now, with no
shopping trip needed.



## Features

- Add, edit and delete pantry items (name, quantity, unit, optional expiry date)
- Pantry list built with a RecyclerView and a custom adapter
- 20 recipes pre-loaded into the database on first run
- **Strict matching:** a recipe is suggested only if every ingredient is in the pantry,
  in at least the required quantity
- Matching copes with plurals ("tomato" and "tomatoes"), capital letters and unit
  differences (g and kg, ml and l, tsp and tbsp)
- Separate "Almost there" list for recipes missing exactly one ingredient (bonus)
- Recipe detail screen with ingredients and numbered method
- Settings screen: expiring-soon highlight toggle and number of days
- Input validation, swipe to delete with Undo, friendly empty states

## Database choice: Room (SQLite)

I chose **Room**, a layer over SQLite that stores the data on the phone.

- It works offline, so the pantry is always available.
- It needs no server, account or internet, so setup is simple.
- Room checks my SQL queries when the app is built, which catches mistakes early.
- It gives full CRUD, and the data is still there after the app is closed and reopened.

Tables: `pantry_item`, `recipe` and `recipe_ingredient` (one recipe has many ingredients).

## How the strict matching works

`RecipeMatcher` adds up the pantry by cleaned ingredient name and unit type, converting
everything to base units (g, ml, piece). For each recipe ingredient it checks the pantry
has at least the required amount. If even one is short, the recipe is left out of the
main list. Nine unit tests in `RecipeMatcherTest` cover this, including the rule that
4 of 5 ingredients is not enough.

## Screens

1. Pantry list
2. Add / Edit ingredient
3. Suggested recipes
4. Recipe detail
5. Settings

## Project structure

- `data/` entities, DAOs, the Room database and the recipe seeder
- `ui/` activities, fragments, adapters and the ViewModel
- `util/` the matcher, the name cleaner and the settings helper

## Setup and run

1. Install [Android Studio](https://developer.android.com/studio).
2. Clone the repository:
   `git clone https://github.com/ThabeloRamanugu/SmartPantryManager.git`
3. Open the folder in Android Studio and wait for the Gradle sync to finish.
4. Create an emulator (for example a Pixel 6) or connect a phone with USB debugging on.
5. Choose the **app** configuration and click **Run**.

To run the tests, right-click `RecipeMatcherTest` and choose **Run**.

## Restrictions followed

The app uses no Google Maps, no mapping SDK and no location or GPS services.

## Author

Thabelo Netshiongolwe