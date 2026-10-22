package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles creating the SQLite database and all pantry CRUD operations.
 * SQLiteOpenHelper calls onCreate() only once, the first time the database is opened.
 * The data is stored in a file on the device, so it survives closing the app.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Pantry table
    public static final String TABLE_PANTRY = "pantry_item";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    // Recipe tables (used later for the strict-matching logic)
    public static final String TABLE_RECIPE = "recipe";
    public static final String TABLE_RECIPE_INGREDIENT = "recipe_ingredient";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_QUANTITY + " REAL NOT NULL, "
                + COL_UNIT + " TEXT NOT NULL, "
                + COL_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "steps TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENT + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "recipe_id INTEGER NOT NULL, "
                + "ingredient_name TEXT NOT NULL, "
                + "quantity REAL NOT NULL, "
                + "unit TEXT NOT NULL, "
                + "FOREIGN KEY(recipe_id) REFERENCES " + TABLE_RECIPE + "(id))");
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple approach: drop and recreate when the schema version changes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ---------- CREATE ----------
    public long addIngredient(Ingredient item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());
        return db.insert(TABLE_PANTRY, null, values); // returns new row id, or -1 on failure
    }

    // ---------- READ ----------
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, COL_NAME + " ASC");
        while (cursor.moveToNext()) {
            list.add(new Ingredient(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY))));
        }
        cursor.close(); // always close cursors to avoid memory leaks
        return list;
    }

    public Ingredient getIngredient(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        Ingredient item = null;
        if (cursor.moveToFirst()) {
            item = new Ingredient(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY)));
        }
        cursor.close();
        return item;
    }

    // ---------- UPDATE ----------
    public int updateIngredient(Ingredient item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());
        return db.update(TABLE_PANTRY, values, COL_ID + "=?",
                new String[]{String.valueOf(item.getId())}); // returns rows affected
    }

    // ---------- DELETE ----------
    public int deleteIngredient(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_ID + "=?", new String[]{String.valueOf(id)});
    }
    // ---------- RECIPE SEEDING (runs once, when the database is first created) ----------
    private void addRecipe(SQLiteDatabase db, String name, String steps, String... ingredients) {
        ContentValues rv = new ContentValues();
        rv.put("name", name);
        rv.put("steps", steps);
        long recipeId = db.insert(TABLE_RECIPE, null, rv);

        for (String line : ingredients) {          // each line is "name|quantity|unit"
            String[] p = line.split("\\|");
            ContentValues iv = new ContentValues();
            iv.put("recipe_id", recipeId);
            iv.put("ingredient_name", p[0]);
            iv.put("quantity", Double.parseDouble(p[1]));
            iv.put("unit", p[2]);
            db.insert(TABLE_RECIPE_INGREDIENT, null, iv);
        }
    }

    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Scrambled Eggs",
                "1. Whisk the eggs with salt.\n2. Melt the butter in a pan.\n3. Pour in the eggs and stir gently until set.",
                "egg|3|piece", "butter|10|g", "salt|1|tsp");
        addRecipe(db, "Tomato Pasta",
                "1. Boil the pasta until soft.\n2. Fry garlic in olive oil, add chopped tomatoes and simmer 10 minutes.\n3. Mix the sauce through the pasta.",
                "pasta|200|g", "tomato|3|piece", "garlic|2|piece", "olive oil|2|tbsp");
        addRecipe(db, "Cheese Omelette",
                "1. Beat the eggs.\n2. Melt butter in a pan and add the eggs.\n3. Sprinkle cheese on top, fold and serve.",
                "egg|3|piece", "cheese|50|g", "butter|10|g");
        addRecipe(db, "Egg Fried Rice",
                "1. Cook the rice and let it cool.\n2. Fry chopped onion, then scramble in the eggs.\n3. Add rice and soy sauce and stir-fry for 5 minutes.",
                "rice|200|g", "egg|2|piece", "onion|1|piece", "soy sauce|2|tbsp");
        addRecipe(db, "Pancakes",
                "1. Mix flour, sugar, milk and egg into a smooth batter.\n2. Pour a ladle into a hot pan.\n3. Flip when bubbles appear and cook the other side.",
                "flour|200|g", "milk|300|ml", "egg|1|piece", "sugar|2|tbsp");
        addRecipe(db, "Mashed Potatoes",
                "1. Peel and boil the potatoes until soft.\n2. Drain and mash with butter and warm milk.",
                "potato|500|g", "butter|30|g", "milk|100|ml");
        addRecipe(db, "Garlic Bread",
                "1. Mash the butter with crushed garlic.\n2. Spread on the bread slices.\n3. Bake at 200 C for 8 minutes.",
                "bread|4|piece", "butter|40|g", "garlic|2|piece");
        addRecipe(db, "Tuna Pasta",
                "1. Boil the pasta.\n2. Drain the tuna and warm it in olive oil.\n3. Toss with the pasta.",
                "pasta|200|g", "tuna|1|piece", "olive oil|1|tbsp");
        addRecipe(db, "Cheese Toastie",
                "1. Butter the outside of the bread.\n2. Put cheese between the slices.\n3. Toast in a pan until golden on both sides.",
                "bread|2|piece", "cheese|40|g", "butter|10|g");
        addRecipe(db, "Vegetable Soup",
                "1. Chop the carrots, potatoes and onion.\n2. Add them to the stock and simmer for 25 minutes.\n3. Season and serve.",
                "carrot|2|piece", "potato|2|piece", "onion|1|piece", "vegetable stock|500|ml");
        addRecipe(db, "Porridge",
                "1. Combine oats and milk in a pot.\n2. Stir over medium heat for 5 minutes.\n3. Sweeten with sugar.",
                "oats|80|g", "milk|300|ml", "sugar|1|tbsp");
        addRecipe(db, "Egg Sandwich",
                "1. Boil the eggs for 8 minutes, then peel and mash.\n2. Butter the bread.\n3. Fill the sandwich with the egg.",
                "bread|2|piece", "egg|2|piece", "butter|10|g");
        addRecipe(db, "Tomato Soup",
                "1. Fry the chopped onion.\n2. Add tomatoes and stock and simmer for 20 minutes.\n3. Blend until smooth.",
                "tomato|6|piece", "onion|1|piece", "vegetable stock|500|ml");
        addRecipe(db, "Rice and Beans",
                "1. Cook the rice.\n2. Fry the onion, add the beans and heat through.\n3. Serve over the rice.",
                "rice|200|g", "beans|1|piece", "onion|1|piece");
        addRecipe(db, "French Toast",
                "1. Whisk eggs and milk together.\n2. Dip the bread slices.\n3. Fry until golden on both sides.",
                "bread|4|piece", "egg|2|piece", "milk|100|ml");
        addRecipe(db, "Loaded Baked Potatoes",
                "1. Pierce the potatoes and bake at 200 C for 60 minutes.\n2. Split open and add butter and grated cheese.",
                "potato|4|piece", "butter|30|g", "cheese|50|g");
    }
    // ---------- RECIPE READING ----------
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor rc = db.query(TABLE_RECIPE, null, null, null, null, null, "name ASC");
        while (rc.moveToNext()) {
            int id = rc.getInt(rc.getColumnIndexOrThrow("id"));
            String name = rc.getString(rc.getColumnIndexOrThrow("name"));
            String steps = rc.getString(rc.getColumnIndexOrThrow("steps"));
            recipes.add(new Recipe(id, name, steps, getRecipeIngredients(db, id)));
        }
        rc.close();
        return recipes;
    }

    public Recipe getRecipe(int recipeId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor rc = db.query(TABLE_RECIPE, null, "id=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        Recipe recipe = null;
        if (rc.moveToFirst()) {
            recipe = new Recipe(recipeId,
                    rc.getString(rc.getColumnIndexOrThrow("name")),
                    rc.getString(rc.getColumnIndexOrThrow("steps")),
                    getRecipeIngredients(db, recipeId));
        }
        rc.close();
        return recipe;
    }

    private List<RecipeIngredient> getRecipeIngredients(SQLiteDatabase db, int recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        Cursor c = db.query(TABLE_RECIPE_INGREDIENT, null, "recipe_id=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        while (c.moveToNext()) {
            list.add(new RecipeIngredient(
                    c.getString(c.getColumnIndexOrThrow("ingredient_name")),
                    c.getDouble(c.getColumnIndexOrThrow("quantity")),
                    c.getString(c.getColumnIndexOrThrow("unit"))));
        }
        c.close();
        return list;
    }
}