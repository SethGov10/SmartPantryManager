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
                        // Edit screen will be added in the next step
                        Toast.makeText(PantryActivity.this,
                                "Edit: " + ingredient.getName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(Ingredient ingredient) {
                        // Delete with confirmation will be added in a later step
                        Toast.makeText(PantryActivity.this,
                                "Delete: " + ingredient.getName(), Toast.LENGTH_SHORT).show();
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