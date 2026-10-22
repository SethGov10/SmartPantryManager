package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 Runs the strict-matching rule against the current pantry and lists
 only the recipes the user can make right now.
 */
public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private TextView tvNoRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.suggestedRoot), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        db = new DatabaseHelper(this);
        tvNoRecipes = findViewById(R.id.tvNoRecipes);

        RecyclerView rv = findViewById(R.id.rvSuggested);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            // Intent carrying the chosen recipe's id to the detail screen
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });
        rv.setAdapter(adapter);
    }

    // Re-run the matching every time the screen appears,
    @Override
    protected void onResume() {
        super.onResume();
        List<Ingredient> pantry = db.getAllIngredients();
        List<Recipe> suggested = new ArrayList<>();
        for (Recipe recipe : db.getAllRecipes()) {
            if (RecipeMatcher.canMake(recipe, pantry)) {
                suggested.add(recipe);
            }
        }
        adapter.setRecipes(suggested);
        tvNoRecipes.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);
    }
}