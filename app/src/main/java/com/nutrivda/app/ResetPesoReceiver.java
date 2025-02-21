package com.nutrivda.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.nutrivda.app.database.DatabaseHelper;

public class ResetPesoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Reiniciar el peso diario en la base de datos
            db.execSQL("UPDATE " + DatabaseHelper.TABLE_PERSONA + " SET " + DatabaseHelper.COLUMN_PESO + " = 0 WHERE id = (SELECT id FROM persona LIMIT 1)");

            // Reiniciar el peso en SharedPreferences para sincronización
            context.getSharedPreferences("PESO_DIARIO", Context.MODE_PRIVATE)
                    .edit()
                    .putFloat("peso", 0.0f)
                    .apply();

            Toast.makeText(context, "Peso diario reiniciado ✅", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al reiniciar el peso", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }
}
