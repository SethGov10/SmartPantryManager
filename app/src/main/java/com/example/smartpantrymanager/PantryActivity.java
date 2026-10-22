package com.example.smartpantrymanager;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PantryActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private IngredientAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        findViewById(R.id.btnAddIngredient).setOnClickListener(v ->
                startActivity(new Intent(PantryActivity.this, AddEditActivity.class)));

        findViewById(R.id.btnSuggested).setOnClickListener(v ->
                startActivity(new Intent(PantryActivity.this, SuggestedRecipesActivity.class)));
        db = new DatabaseHelper(this);
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rv = findViewById(R.id.rvPantry);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new IngredientAdapter(db.getAllIngredients(),
                new IngredientAdapter.OnItemActionListener() {
                    @Override
                    public void onItemClick(Ingredient ingredient) {
                        Intent intent = new Intent(PantryActivity.this, AddEditActivity.class);
                        intent.putExtra(AddEditActivity.EXTRA_INGREDIENT_ID, ingredient.getId());
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(Ingredient ingredient) {
                        new androidx.appcompat.app.AlertDialog.Builder(PantryActivity.this)
                                .setTitle("Delete ingredient")
                                .setMessage("Delete " + ingredient.getName() + "?")
                                .setPositiveButton("Delete", (dialog, which) -> {
                                    db.deleteIngredient(ingredient.getId());
                                    refreshList();
                                    Toast.makeText(PantryActivity.this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                });
        rv.setAdapter(adapter);
    }

    // on Resume runs every time the screen comes back into view
    //  the list refreshes after adding or editing an item
    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        List<Ingredient> items = db.getAllIngredients();
        adapter.setItems(items);
        // Show the empty-state message when there are no items
        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }
}