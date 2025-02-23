package com.nutrivda.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeResponse {
    @SerializedName("hits")
    private List<Hit> hits;

    public List<Hit> getHits() {
        return hits;
    }

    public static class Hit {
        @SerializedName("recipe")
        private Recipe recipe;

        public Recipe getRecipe() {
            return recipe;
        }
    }

    public static class Recipe {
        @SerializedName("label")
        private String label;

        @SerializedName("calories")
        private double calories;

        public String getLabel() {
            return label;
        }

        public double getCalories() {
            return calories;
        }
    }
}
