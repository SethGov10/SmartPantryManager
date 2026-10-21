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
}