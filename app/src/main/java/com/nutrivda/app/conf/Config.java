package com.nutrivda.app.conf;

import android.content.Context;

import java.io.IOException;
import java.util.Properties;

public class Config {
    private static Properties properties = new Properties();

    public static void loadProperties(Context context) {
        try {
            properties.load(context.getAssets().open("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSupabaseUrl() {
        return properties.getProperty("SUPABASE_URL", "https://default.supabase.co");
    }

    public static String getSupabaseKey() {
        return properties.getProperty("SUPABASE_KEY", "default-key");
    }
}
