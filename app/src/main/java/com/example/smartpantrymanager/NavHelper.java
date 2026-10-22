package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Wires up the bottom navigation bar on any screen. Each tab uses an Intent
 * to open the matching Activity, so all three screens share one piece of code.
 */
public class NavHelper {

    public static void setup(Activity activity, int selectedItemId) {
        BottomNavigationView nav = activity.findViewById(R.id.bottomNav);
        nav.setSelectedItemId(selectedItemId); // highlight the current tab

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedItemId) return true; // already on this screen

            Class<?> target;
            if (id == R.id.nav_pantry) target = PantryActivity.class;
            else if (id == R.id.nav_suggestions) target = SuggestedRecipesActivity.class;
            else target = SettingsActivity.class;

            Intent intent = new Intent(activity, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.startActivity(intent);
            return true;
        });
    }
}