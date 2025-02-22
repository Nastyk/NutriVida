package com.nutrivda.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "controlpeso.db";
    private static final int DATABASE_VERSION = 1; // Aumentamos la versión para aplicar mejoras

    // Tabla Persona
    public static final String TABLE_PERSONA = "persona";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_ALTURA = "altura";
    public static final String COLUMN_PESO = "peso";
    public static final String COLUMN_PASSWORD = "password";

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
    public static final String COLUMN_DESAYUNO = "desayuno";
    public static final String COLUMN_COMIDA = "comida";
    public static final String COLUMN_CENA = "cena";
    public static final String COLUMN_SW_DESAYUNO = "swdesayuno";
    public static final String COLUMN_SW_COMIDA = "swcomida";
    public static final String COLUMN_SW_CENA = "swcena";

    // Sentencias SQL para crear las tablas
    private static final String CREATE_TABLE_PERSONA =
            "CREATE TABLE " + TABLE_PERSONA + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE + " TEXT NOT NULL, " +
                    COLUMN_ALTURA + " REAL NOT NULL, " +
                    COLUMN_PESO + " REAL NOT NULL," +
                    COLUMN_PASSWORD + " TEXT NOT NULL);";

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
                    COLUMN_DESAYUNO + " TEXT, " +
                    COLUMN_COMIDA + " TEXT, " +
                    COLUMN_CENA + " TEXT, " +
                    COLUMN_SW_DESAYUNO + " INTEGER DEFAULT 0, " +
                    COLUMN_SW_COMIDA + " INTEGER DEFAULT 0, " +
                    COLUMN_SW_CENA + " INTEGER DEFAULT 0, " +
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
    }

    public void insertarComidasPorDefecto(SQLiteDatabase db) {
        String checkQuery = "SELECT COUNT(*) FROM " + TABLE_COMIDAS;
        Cursor cursor = db.rawQuery(checkQuery, null);
        boolean hayDatos = false;

        try {
            if (cursor.moveToFirst()) {
                hayDatos = cursor.getInt(0) > 0;
            }
        } finally {
            cursor.close();
        }

        if (!hayDatos) {
            db.beginTransaction();

            try {
                String sql = "INSERT INTO " + TABLE_COMIDAS + " (nombre, momento, kcal) VALUES (?, ?, ?)";
                SQLiteStatement stmt = db.compileStatement(sql);

                String[][] comidas = {
                        {"Avena con frutas", "Desayuno", "370"},
                        {"Huevo revuelto con pan", "Desayuno", "320"},
                        {"Ensalada de pollo", "Comida", "500"},
                        {"Pechuga de pollo con arroz", "Comida", "623"},
                        {"Sopa de verduras", "Cena", "250"},
                        {"Atún con ensalada", "Cena", "300"},
                        {"Tostadas con aguacate y huevo", "Desayuno", "350"},
                        {"Panqueques de avena con miel", "Desayuno", "400"},
                        {"Batido de plátano con proteína y almendras", "Desayuno", "300"},
                        {"Yogur con granola y frutos secos", "Desayuno", "320"},
                        {"Arepa con queso y jamón de pavo", "Desayuno", "330"},
                        {"Pollo a la plancha con quinoa y verduras", "Comida", "500"},
                        {"Salmón al horno con ensalada y arroz integral", "Comida", "720"},
                        {"Pasta integral con salsa de tomate y albóndigas de pavo", "Comida", "580"},
                        {"Tacos de pescado con ensalada y guacamole", "Comida", "500"},
                        {"Bowl de arroz con pollo teriyaki y brócoli", "Comida", "620"},
                        {"Ensalada de atún con tomate, lechuga y huevo cocido", "Cena", "314"},
                        {"Sopa de calabaza con semillas de girasol", "Cena", "300"},
                        {"Tostadas de pan integral con aguacate y queso cottage", "Cena", "320"},
                        {"Rollitos de jamón con queso y frutos secos", "Cena", "285"},
                        {"Pechuga de pollo con espárragos y puré de papa", "Cena", "400"}
                };

                for (String[] comida : comidas) {
                    stmt.bindString(1, comida[0]);
                    stmt.bindString(2, comida[1]);
                    stmt.bindLong(3, Integer.parseInt(comida[2]));
                    stmt.executeInsert();
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }
}
