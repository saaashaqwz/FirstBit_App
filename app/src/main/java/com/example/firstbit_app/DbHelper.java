package com.example.firstbit_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.firstbit_app.Models.User;

/**
 * вспомогательный класс для работы с SQLite
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    /**
     * создаёт таблицы
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        android.util.Log.d("DbHelper", "Создание таблиц базы данных...");

        // таблица пользователей
        String CREATE_USERS_TABLE = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY, " +
                "login TEXT, " +
                "phone TEXT, " +
                "password TEXT)";
        db.execSQL(CREATE_USERS_TABLE);
        android.util.Log.d("DbHelper", "Таблица пользователей создана");
        initializeData(db);
    }

    /**
     * обновляет схему БД при изменении версии
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        android.util.Log.d("DbHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);

        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    /**
     * метод инициализации данных
     */
    private void initializeData(SQLiteDatabase db) {
        android.util.Log.d("DbHelper", "Инициализация данных...");
    }

    /**
     * методы для работы с пользователями
     */
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("login", user.getLogin());
        values.put("phone", user.getPhone());
        values.put("password", user.getPassword());
        db.insert("users", null, values);
        db.close();
    }

    public boolean getUser(String login, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE login = ? AND password = ?",
                new String[]{login, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }
}
