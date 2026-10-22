package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.regex.Pattern;

/**
 * Screen used for both adding a new ingredient and editing an existing one.
 * If the Intent that started this screen carries an "ingredient_id" extra,
 * we are in EDIT mode; otherwise we are in ADD mode.
 */
public class AddEditActivity extends AppCompatActivity {

    public static final String EXTRA_INGREDIENT_ID = "ingredient_id";

    private static final String[] UNITS = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "piece"};

    private DatabaseHelper db;
    private EditText etName, etQuantity, etExpiry;
    private Spinner spUnit;
    private Button btnDelete;
    private int editId = -1; // -1 means "adding a new item"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addEditRoot), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        db = new DatabaseHelper(this);

        TextView tvFormTitle = findViewById(R.id.tvFormTitle);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);
        Button btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Fill the unit dropdown
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, UNITS);
        spUnit.setAdapter(unitAdapter);

        // Did the previous screen send us an ingredient id? If so, we are editing.
        editId = getIntent().getIntExtra(EXTRA_INGREDIENT_ID, -1);
        if (editId != -1) {
            Ingredient existing = db.getIngredient(editId);
            if (existing != null) {
                tvFormTitle.setText("Edit Ingredient");
                etName.setText(existing.getName());
                etQuantity.setText(String.valueOf(existing.getQuantity()));
                etExpiry.setText(existing.getExpiryDate());
                spUnit.setSelection(indexOfUnit(existing.getUnit()));
                btnDelete.setVisibility(View.VISIBLE);
            }
        }

        btnSave.setOnClickListener(v -> saveIngredient());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private int indexOfUnit(String unit) {
        for (int i = 0; i < UNITS.length; i++) {
            if (UNITS[i].equalsIgnoreCase(unit)) return i;
        }
        return 0;
    }

    /** Validates the form, then inserts or updates the record. */
    private void saveIngredient() {
        String name = etName.getText().toString().trim();
        String qtyText = etQuantity.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();
        String unit = spUnit.getSelectedItem().toString();

        // ---- Input validation ----
        if (name.isEmpty()) {
            etName.setError("Please enter an ingredient name");
            etName.requestFocus();
            return;
        }
        if (qtyText.isEmpty()) {
            etQuantity.setError("Please enter a quantity");
            etQuantity.requestFocus();
            return;
        }
        double quantity;
        try {
            quantity = Double.parseDouble(qtyText);
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number");
            etQuantity.requestFocus();
            return;
        }
        if (quantity <= 0) {
            etQuantity.setError("Quantity must be greater than 0");
            etQuantity.requestFocus();
            return;
        }
        // Expiry is optional, but if given it must look like YYYY-MM-DD
        if (!expiry.isEmpty() && !Pattern.matches("\\d{4}-\\d{2}-\\d{2}", expiry)) {
            etExpiry.setError("Use the format YYYY-MM-DD");
            etExpiry.requestFocus();
            return;
        }

        if (editId == -1) {
            db.addIngredient(new Ingredient(name, quantity, unit, expiry));
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            db.updateIngredient(new Ingredient(editId, name, quantity, unit, expiry));
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish(); // close this screen; PantryActivity.onResume() refreshes the list
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.deleteIngredient(editId);
                    Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}