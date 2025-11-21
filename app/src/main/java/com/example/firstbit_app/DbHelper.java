package com.example.firstbit_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.firstbit_app.Models.Category;
import com.example.firstbit_app.Models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * вспомогательный класс для работы с SQLite
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 2;

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

        // таблица категорий
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE categories (" +
                "id INTEGER PRIMARY KEY, " +
                "title TEXT)";
        db.execSQL(CREATE_CATEGORIES_TABLE);
        android.util.Log.d("DbHelper", "Таблица категорий создана");

        initializeData(db);
    }

    /**
     * обновляет схему БД при изменении версии
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        android.util.Log.d("DbHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);

        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS categories");

        onCreate(db);
    }

    /**
     * метод инициализации данных
     */
    private void initializeData(SQLiteDatabase db) {
        android.util.Log.d("DbHelper", "Инициализация данных...");

        initializeCategories(db);
    }

    /**
     * метод для добавления пользователя
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

    /**
     * метод для получения пользователя
     */
    public boolean getUser(String login, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE login = ? AND password = ?",
                new String[]{login, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    /**
     * проверяет существование пользователя с указанным логином в БД
     */
    public boolean isLoginExists(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE login = ?", new String[]{login});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    /**
     * проверяет существование пользователя с указанным телефоном в БД
     */
    public boolean isPhoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE phone = ?", new String[]{phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    /**
     * возвращает id пользователя по логину
     */
    public int getUserId(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM users WHERE login = ?", new String[]{login});
        int id = -1;
        if (c.moveToFirst()) id = c.getInt(0);
        c.close();
        db.close();
        return id;
    }

    /**
     * метод инициализации категорий
     */
    private void initializeCategories(SQLiteDatabase db) {
        android.util.Log.d("DbHelper", "Инициализация категорий...");

        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Услуги"));
        categories.add(new Category(2, "Бухгалтерия"));
        categories.add(new Category(3, "Кадры"));
        categories.add(new Category(4, "Торговля"));
        categories.add(new Category(5, "Склад"));
        categories.add(new Category(6, "Производство"));

        for (Category category : categories) {
            ContentValues values = new ContentValues();
            values.put("id", category.getId());
            values.put("title", category.getTitle());
            db.insert("categories", null, values);
        }
        android.util.Log.d("DbHelper", "Категории инициализированы: " + categories.size());
    }

    /**
     * метод для получения всех категорий
     */
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM categories ORDER BY id", null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(0));
                category.setTitle(cursor.getString(1));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return categoryList;
    }
}
