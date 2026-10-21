package com.thabelo.smartpantry.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

// I use this class to clean ingredient names before I compare them.
// "  Tomatoes " becomes "tomato", so it matches the recipe's "Tomato".
public final class IngredientNormalizer {

    // These words end in "s" but are already singular, so I leave them alone.
    private static final Set<String> UNCHANGED = new HashSet<>(Arrays.asList(
            "hummus", "couscous", "asparagus", "molasses", "watercress"));

    // I made the constructor private because I only use static methods here.
    private IngredientNormalizer() { }

    // I make the name lowercase, remove extra spaces, then fix the last word.
    public static String normalize(String raw) {
        if (raw == null) return "";

        // lowercase, trim, and squash many spaces into one
        String s = raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (s.isEmpty()) return s;

        // I only change the last word: "cherry tomatoes" -> "cherry tomato"
        int lastSpace = s.lastIndexOf(' ');
        String head = lastSpace >= 0 ? s.substring(0, lastSpace + 1) : "";
        String last = lastSpace >= 0 ? s.substring(lastSpace + 1) : s;
        return head + singular(last);
    }

    // My simple plural rules. They're enough for this app.
    private static String singular(String w) {
        if (UNCHANGED.contains(w) || w.length() <= 3) return w;
        if (w.endsWith("ies")) return w.substring(0, w.length() - 3) + "y"; // berries -> berry
        if (w.endsWith("oes")) return w.substring(0, w.length() - 2);       // tomatoes -> tomato
        if (w.endsWith("ss") || w.endsWith("us")) return w;                 // watercress stays
        if (w.endsWith("s")) return w.substring(0, w.length() - 1);         // eggs -> egg
        return w;
    }
}