# Smart Pantry Manager

An Android application written in Java that helps reduce food waste. The user records the ingredients they already have at home (their "pantry"), and the app suggests only the recipes they can cook **right now**, with no shopping trip required.

Built for **Mobile App Development 700 (Richfield Graduate Institute of Technology).

**Author: Seth Govender | **Student number:402413237

## Features

- Pantry management:add, edit and delete ingredients (name, quantity, unit, optional expiry date), with input validation on the form.
- Pantry list: a RecyclerView with a custom adapter, bound to the database.
- Recipe collection: 16 recipes pre-loaded into the database on first run.
- Suggested Recipes:runs the strict-matching rule against the current pantry and lists only recipes the user can make.
- Recipe Detail:** shows the full ingredient list and method of a selected recipe.
- Empty-state feedback:** a clear message appears when no recipes match.
- Settings:** highlight items expiring within 3 days (toggle), and a default unit for new ingredients. Both are saved with SharedPreferences.
- Bottom navigation bar:** Pantry, Suggestions and Settings tabs.

## The strict-matching rule

A recipe is suggested only if every ingredient it needs is in the pantry, in at least the required quantity**. If a recipe needs 5 ingredients and the pantry has 4, it is not suggested.

The matching is also robust to simple real-world differences:

- **Case and plurals:** "Tomatoes", "tomato" and "TOMATO" are treated as the same ingredient.
- **Unit conversion:** for example, 0.1 kg of cheese satisfies a recipe that needs 50 g.
- **Duplicate entries:** if the same ingredient is entered twice, the quantities are added together.
- **Incompatible units:** weight, volume and count units are not compared with each other.

The logic is in `RecipeMatcher.java` and is covered by unit tests in `RecipeMatcherTest.java`.

## Database choice: SQLite

This app uses **SQLite** through `SQLiteOpenHelper`, stored locally on the device.

**Why SQLite:**

- All the data (pantry items and recipes) belongs to one user on one device, so no server or cloud sync is needed.
- It works offline, which suits a kitchen app.
- It needs no accounts, hosting or API keys, so the project is simple to set up and run.
- Data persists after the app is closed and reopened, which satisfies the persistence requirement.

**Tables:**

| Table | Columns |
|---|---|
| `pantry_item` | id, name, quantity, unit, expiry_date |
| `recipe` | id, name, steps |
| `recipe_ingredient` | id, recipe_id (foreign key), ingredient_name, quantity, unit |

## Screens

1. **Pantry List** (`PantryActivity`)
2. **Add/Edit Ingredient** (`AddEditActivity`)
3. **Suggested Recipes** (`SuggestedRecipesActivity`)
4. **Recipe Detail** (`RecipeDetailActivity`)
5. **Settings** (`SettingsActivity`)

Screens are connected with Intents, and the bottom navigation bar is set up by `NavHelper`.

## Setup and run instructions

**Requirements**

- Android Studio (recent version) with the Android SDK installed
- An Android phone with USB debugging turned on, or an Android emulator
- Minimum SDK: 37

**Steps**

1. Clone the repository:
```
   git clone https://github.com/SethGov10/SmartPantryManager.git
```
2. Open Android Studio and choose **File → Open**, then select the cloned `SmartPantryManager` folder.
3. Wait for Gradle to finish syncing (internet is needed the first time).
4. Connect your phone, or start an emulator.
5. Select the **app** run configuration and press **Run**.
6. On first launch the app creates its database and loads the 16 recipes automatically.

**Running the unit tests**

Right-click `RecipeMatcherTest` (under `app/src/test`) and choose **Run 'RecipeMatcherTest'**.

**Quick demo**

1. Add `egg` (3 piece), `cheese` (50 g) and `butter` (10 g) to the pantry.
2. Open the **Suggestions** tab: **Cheese Omelette** appears.
3. Delete the butter, then reopen **Suggestions**: the omelette disappears.

## Project structure

```
app/src/main/java/com/example/smartpantrymanager/
    PantryActivity.java            pantry list screen
    AddEditActivity.java           add / edit form with validation
    SuggestedRecipesActivity.java  suggested recipes screen
    RecipeDetailActivity.java      recipe detail screen
    SettingsActivity.java          settings screen
    DatabaseHelper.java            SQLite setup, CRUD and recipe seeding
    RecipeMatcher.java             strict-matching logic
    IngredientAdapter.java         RecyclerView adapter for the pantry
    RecipeAdapter.java             RecyclerView adapter for recipes
    NavHelper.java                 bottom navigation helper
    Ingredient.java, Recipe.java, RecipeIngredient.java    model classes
```

## Scope

As required by in my assignment, the app does not use Google Maps, any mapping SDK, or location/GPS services. Its scope is strictly the user's own pantry and recipe matching.