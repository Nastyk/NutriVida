package com.nutrivda.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "controlpeso.db";
    private static final int DATABASE_VERSION = 4; // Aumentamos la versión para aplicar mejoras

    // Tabla Persona
    public static final String TABLE_PERSONA = "persona";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_ALTURA = "altura";
    public static final String COLUMN_PESO = "peso";

    // Tabla Comidas
    public static final String TABLE_COMIDAS = "comidas";
    public static final String COLUMN_COMIDA_ID = "id";
    public static final String COLUMN_NOMBRE_COMIDA = "nombre";
    public static final String COLUMN_MOMENTO_DIA = "momento";
    public static final String COLUMN_KCAL = "kcal";

    // Tabla de Días Completados
    public static final String TABLE_DIAS_COMPLETADOS = "dias_completados";
    public static final String COLUMN_FECHA = "fecha";
    public static final String COLUMN_COMPLETADO = "completado";
    public static final String COLUMN_PERSONA_ID = "persona_id"; // Relación con persona

    // Sentencias SQL para crear las tablas
    private static final String CREATE_TABLE_PERSONA =
            "CREATE TABLE " + TABLE_PERSONA + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE + " TEXT NOT NULL, " +
                    COLUMN_ALTURA + " REAL NOT NULL, " +
                    COLUMN_PESO + " REAL NOT NULL);";

    private static final String CREATE_TABLE_COMIDAS =
            "CREATE TABLE " + TABLE_COMIDAS + " (" +
                    COLUMN_COMIDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE_COMIDA + " TEXT NOT NULL, " +
                    COLUMN_MOMENTO_DIA + " TEXT NOT NULL, " +
                    COLUMN_KCAL + " INTEGER NOT NULL);";

    private static final String CREATE_TABLE_DIAS_COMPLETADOS =
            "CREATE TABLE " + TABLE_DIAS_COMPLETADOS + " (" +
                    COLUMN_FECHA + " TEXT PRIMARY KEY, " +
                    COLUMN_COMPLETADO + " INTEGER DEFAULT 0, " +
                    COLUMN_PERSONA_ID + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_PERSONA_ID + ") REFERENCES " + TABLE_PERSONA + "(" + COLUMN_ID + ") ON DELETE CASCADE);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PERSONA);
        db.execSQL(CREATE_TABLE_COMIDAS);
        db.execSQL(CREATE_TABLE_DIAS_COMPLETADOS);
        insertarComidasPorDefecto(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            // Se eliminan las tablas antiguas y se recrean con mejoras
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIAS_COMPLETADOS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMIDAS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSONA);
            onCreate(db);
        }
    }

    public void insertarComidasPorDefecto(SQLiteDatabase db) {
        // Verificar si ya hay comidas registradas para evitar duplicados
        String checkQuery = "SELECT COUNT(*) FROM " + TABLE_COMIDAS;
        Cursor cursor = db.rawQuery(checkQuery, null);
        boolean hayDatos = false;

        if (cursor.moveToFirst()) {
            hayDatos = cursor.getInt(0) > 0;
        }
        cursor.close();

        if (!hayDatos) { // Solo insertar comidas si la tabla está vacía
            String[] comidas = {
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Avena con frutas', 'Desayuno', 370);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Huevo revuelto con pan', 'Desayuno', 320);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Ensalada de pollo', 'Comida', 500);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Pechuga de pollo con arroz', 'Comida', 623);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Sopa de verduras', 'Cena', 250);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Atún con ensalada', 'Cena', 300);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Tostadas con aguacate y huevo', 'Desayuno', 350);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Panqueques de avena con miel', 'Desayuno', 400);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Batido de plátano con proteína y almendras', 'Desayuno', 300);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Yogur con granola y frutos secos', 'Desayuno', 320);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Arepa con queso y jamón de pavo', 'Desayuno', 330);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Pollo a la plancha con quinoa y verduras', 'Comida', 500);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Salmón al horno con ensalada y arroz integral', 'Comida', 720);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Pasta integral con salsa de tomate y albóndigas de pavo', 'Comida', 580);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Tacos de pescado con ensalada y guacamole', 'Comida', 500);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Bowl de arroz con pollo teriyaki y brócoli', 'Comida', 620);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Ensalada de atún con tomate, lechuga y huevo cocido', 'Cena', 314);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Sopa de calabaza con semillas de girasol', 'Cena', 300);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Tostadas de pan integral con aguacate y queso cottage', 'Cena', 320);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Rollitos de jamón con queso y frutos secos', 'Cena', 285);",
                    "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES ('Pechuga de pollo con espárragos y puré de papa', 'Cena', 400);"
            };

            for (String comida : comidas) {
                db.execSQL(comida);
            }
        }
    }
}
