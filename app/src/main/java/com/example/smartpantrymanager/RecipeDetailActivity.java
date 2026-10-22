package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Shows the full ingredient list and method for one recipe.
 * The recipe id arrives through the Intent sent by SuggestedRecipesActivity.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailRoot), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // Read the recipe id that the previous screen put into the Intent
        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = new DatabaseHelper(this).getRecipe(recipeId);

        if (recipe == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ((TextView) findViewById(R.id.tvDetailName)).setText(recipe.getName());

        // Build the ingredient list, one line per ingredient
        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ri : recipe.getIngredients()) {
            sb.append("• ")
                    .append(ri.getName())
                    .append(" - ")
                    .append(ri.getQuantity())
                    .append(" ")
                    .append(ri.getUnit())
                    .append("\n");
        }
        ((TextView) findViewById(R.id.tvDetailIngredients)).setText(sb.toString().trim());
        ((TextView) findViewById(R.id.tvDetailSteps)).setText(recipe.getSteps());
    }
}