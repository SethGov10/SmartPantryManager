package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Settings screen. to turn it off or on
 */
public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS = "settings";
    public static final String KEY_ALERTS = "expiry_alerts";
    public static final String KEY_UNIT = "default_unit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settingsRoot), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        NavHelper.setup(this, R.id.nav_settings);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // Expiry alerts switch it loads the saved value and it save on every change
        SwitchCompat switchAlerts = findViewById(R.id.switchAlerts);
        switchAlerts.setChecked(prefs.getBoolean(KEY_ALERTS, true));
        switchAlerts.setOnCheckedChangeListener((button, checked) ->
                prefs.edit().putBoolean(KEY_ALERTS, checked).apply());

        // Default unit spinner
        Spinner spUnit = findViewById(R.id.spDefaultUnit);
        spUnit.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, AddEditActivity.UNITS));
        String saved = prefs.getString(KEY_UNIT, "g");
        for (int i = 0; i < AddEditActivity.UNITS.length; i++) {
            if (AddEditActivity.UNITS[i].equals(saved)) spUnit.setSelection(i);
        }
        spUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putString(KEY_UNIT, AddEditActivity.UNITS[position]).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }
}