package com.example.smartpantrymanager;

import java.util.List;

/**
 * Implements the strict-matching rule: a recipe can be made ONLY if every
 * ingredient it needs is in the pantry, in at least the required quantity.
 */
public class RecipeMatcher {

    /** Returns true only if ALL of the recipe's ingredients are covered by the pantry. */
    public static boolean canMake(Recipe recipe, List<Ingredient> pantry) {
        for (RecipeIngredient needed : recipe.getIngredients()) {
            double have = 0;
            String neededName = normalize(needed.getName());
            String neededType = unitType(needed.getUnit());

            // Add up every pantry entry with the same (normalised) name and a compatible unit type
            for (Ingredient item : pantry) {
                if (normalize(item.getName()).equals(neededName)
                        && unitType(item.getUnit()).equals(neededType)) {
                    have += toBaseUnit(item.getQuantity(), item.getUnit());
                }
            }

            double required = toBaseUnit(needed.getQuantity(), needed.getUnit());
            if (have + 0.0001 < required) {
                return false; // one missing or insufficient ingredient = NOT suggested
            }
        }
        return true;
    }

    /** Makes "Tomatoes", " tomato " and "TOMATO" all become "tomato". */
    public static String normalize(String name) {
        String s = name.trim().toLowerCase().replaceAll("\\s+", " ");
        if (s.endsWith("ies") && s.length() > 4) return s.substring(0, s.length() - 3) + "y"; // berries -> berry
        if (s.endsWith("oes")) return s.substring(0, s.length() - 2);                       // tomatoes -> tomato
        if (s.endsWith("s") && !s.endsWith("ss")) return s.substring(0, s.length() - 1);    // eggs -> egg
        return s;
    }

    /** Weight, volume and count units can't be compared with each other. */
    private static String unitType(String unit) {
        switch (unit.toLowerCase()) {
            case "g": case "kg": return "weight";
            case "ml": case "l": case "tsp": case "tbsp": case "cup": return "volume";
            default: return "count"; // piece
        }
    }

    /** Converts to grams, millilitres or pieces so quantities can be compared. */
    private static double toBaseUnit(double qty, String unit) {
        switch (unit.toLowerCase()) {
            case "kg": return qty * 1000;
            case "l": return qty * 1000;
            case "tsp": return qty * 5;
            case "tbsp": return qty * 15;
            case "cup": return qty * 240;
            default: return qty; // g, ml, piece
        }
    }
}