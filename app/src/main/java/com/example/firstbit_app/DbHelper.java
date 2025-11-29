package com.example.firstbit_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.firstbit_app.Models.Cart;
import com.example.firstbit_app.Models.Category;
import com.example.firstbit_app.Models.Product;
import com.example.firstbit_app.Models.Service;
import com.example.firstbit_app.Models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * вспомогательный класс для работы с SQLite
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 4;

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

        // таблица продуктов
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY, " +
                "image TEXT, " +
                "category_id INTEGER, " +
                "title TEXT, " +
                "description TEXT, " +
                "license TEXT, " +
                "price INTEGER, " +
                "FOREIGN KEY(category_id) REFERENCES categories(id))";
        db.execSQL(CREATE_PRODUCTS_TABLE);
        android.util.Log.d("DbHelper", "Таблица продуктов создана");

        // таблица услуг
        String CREATE_SERVICES_TABLE = "CREATE TABLE services (" +
                "id INTEGER PRIMARY KEY, " +
                "category_id INTEGER, " +
                "title TEXT, " +
                "deadline TEXT, " +
                "price INTEGER, " +
                "FOREIGN KEY(category_id) REFERENCES categories(id))";
        db.execSQL(CREATE_SERVICES_TABLE);
        android.util.Log.d("DbHelper", "Таблица услуг создана");

        // таблица корзины
        String CREATE_CART_TABLE = "CREATE TABLE cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "product_id INTEGER, " +
                "service_id INTEGER, " +
                "quantity INTEGER DEFAULT 1, " +
                "added_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(user_id) REFERENCES users(id), " +
                "FOREIGN KEY(product_id) REFERENCES products(id), " +
                "FOREIGN KEY(service_id) REFERENCES services(id))";
        db.execSQL(CREATE_CART_TABLE);
        android.util.Log.d("DbHelper", "Таблица корзин создана");

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
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS services");
        db.execSQL("DROP TABLE IF EXISTS cart");

        onCreate(db);
    }

    /**
     * метод инициализации данных
     */
    private void initializeData(SQLiteDatabase db) {
        android.util.Log.d("DbHelper", "Инициализация данных...");

        initializeCategories(db);
        initializeProducts(db);
        initializeServices(db);
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

    private void initializeProducts(SQLiteDatabase db) {
        android.util.Log.d("DbHelper", "Initializing products...");

        List<Product> products = new ArrayList<>();

        products.add(new Product(1, "product_buh_base.png", 2, "Бухгалтерия", "1С:Бухгалтерия БАЗОВАЯ", "Базовая версия для малого бизнеса и ИП. Однопользовательская версия с основным набором функций для ведения учёта.", "Постоянная лицензия на 1 рабочее место", 4400));
        products.add(new Product(2, "product_buh_corp.png", 2, "Бухгалтерия", "1С:Бухгалтерия ПРОФ", "Профессиональная версия для ведения полноценного бухгалтерского и налогового учёта. Поддержка многопользовательской работы.", "Постоянная лицензия на 1 рабочее место", 20100));
        products.add(new Product(3, "product_buh_corp.png", 2, "Бухгалтерия", "1С:Бухгалтерия КОРП", "Корпоративная версия для распределённых компаний с сложной учётной структурой. Расширенный функционал для консолидации.", "Постоянная лицензия на 1 рабочее место", 51700));
        products.add(new Product(9, "product_reporting.png", 2, "Бухгалтерия", "1С:Отчётность для ЮР.ЛИЦ", "Надстройка для удобной подготовки и отправки регламентированной отчётности (налоговой, бухгалтерской, статистической) в госорганы напрямую из 1С.", "Годовая подписка на 1 рабочее место (ИТС)", 6600));
        products.add(new Product(10, "product_reporting.png", 2, "Бухгалтерия", "1С:Отчётность для ИП", "Специализированная версия для индивидуальных предпринимателей по подготовке и отправке отчётности в госорганы.", "Годовая подписка на 1 рабочее место (ИТС)", 2400));

        products.add(new Product(4, "product_zup_prof.png", 3, "Кадры", "1С:Зарплата и управление персоналом БАЗОВАЯ", "Базовая версия для малого бизнеса. Расчёт зарплаты, кадровый учёт и подготовка отчётности для небольшого штата.", "Постоянная лицензия на 1 рабочее место", 10800));
        products.add(new Product(5, "product_zup_prof.png", 3, "Кадры", "1С:Зарплата и управление персоналом ПРОФ", "Профессиональная версия с полным функционалом для расчёта зарплаты, управления персоналом и кадрового делопроизводства.", "Постоянная лицензия на 1 рабочее место", 34800));
        products.add(new Product(6, "product_zup_corp.png", 3, "Кадры", "1С:Зарплата и управление персоналом КОРП", "Мощное решение для холдингов и крупных компаний с распределённой структурой. Поддержка МСФО и сложных систем мотивации.", "Постоянная лицензия на 1 рабочее место", 167400));

        products.add(new Product(7, "product_management_base.png", 4, "Торговля", "1С:Управление торговлей БАЗОВАЯ", "Решение для автоматизации оперативного учёта в небольших торговых компаниях. Учёт продаж, закупок и движения товаров.", "Постоянная лицензия на 1 рабочее место", 9700));
        products.add(new Product(8, "product_management_prof.png", 4, "Торговля", "1С:Управление торговлей ПРОФ", "Полнофункциональная система для автоматизации розничной и оптовой торговли. Управление отношениями с клиентами (CRM), маркетинг и аналитика.", "Постоянная лицензия на 1 рабочее место", 34800));

        products.add(new Product(12, "product_logist_base.png", 5, "Склад", "1С:Логистика. Управление складом БАЗОВАЯ", "Автоматизация складских операций: от приёмки до отгрузки. Подходит для малых и средних складов.", "Постоянная лицензия на 1 рабочее место", 55900));
        products.add(new Product(13, "product_wms.png", 5, "Склад", "1С:WMS Логистика. Управление складом", "Профессиональная система управления складом (WMS). Оптимизация зонирования, маршрутизации, работа с радиотерминалами.", "Постоянная лицензия на 1 рабочее место", 367700));

        products.add(new Product(11, "product_automation.png", 6, "Производство", "1С:Комплексная автоматизация", "Единая система для управления финансами, продажами, закупками, производством и запасами для среднего бизнеса.", "Постоянная лицензия на 1 рабочее место", 94700));
        products.add(new Product(14, "product_firma_base.png", 6, "Производство", "1С:Управление нашей фирмой БАЗОВАЯ", "Готовое решение для малого бизнеса. Учёт продаж, закупок, денежных средств, клиентов и сделок без глубоких настроек.", "Постоянная лицензия на 1 рабочее место", 7300));
        products.add(new Product(15, "product_firma_prof.png", 6, "Производство", "1С:Управление нашей фирмой ПРОФ", "Расширенная версия для управления малым бизнесом с возможностью глубокой настройки под специфику компании.", "Постоянная лицензия на 1 рабочее место", 26800));
        products.add(new Product(16, "product_ohrana.png", 6, "Производство", "1С:Производственная безопасность. Охрана труда", "Автоматизация управления охраной труда и промышленной безопасностью. Учет СИЗ, планирование инструктажей, управление опасными объектами, формирование отчетности для Ростехнадзора и Минтруда.", "Постоянная лицензия на 1 рабочее место", 45500));

        for (Product product : products) {
            ContentValues values = new ContentValues();
            values.put("id", product.getId());
            values.put("image", product.getImage());
            values.put("category_id", product.getCategory().getId());
            values.put("title", product.getTitle());
            values.put("description", product.getDescription());
            values.put("license", product.getLicense());
            values.put("price", product.getPrice());
            db.insert("products", null, values);
        }
        android.util.Log.d("DbHelper", "Products initialized: " + products.size());
    }

    /**
     * метод для получения всех продуктов
     */
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT p.*, c.title as category_title FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "ORDER BY p.id";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(7),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(6)
                );
                productList.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return productList;
    }

    private void initializeServices(SQLiteDatabase db) {
        android.util.Log.d("DbHelper", "Initializing services...");

        List<Service> services = new ArrayList<>();

        services.add(new Service(1, 1,"Услуги","Внедрение 1С:Бухгалтерия", "5-10 рабочих дней", 25000));
        services.add(new Service(2, 1, "Услуги","Обновление конфигураций 1С", "1-3 рабочих дня", 5000));
        services.add(new Service(3, 1, "Услуги","Доработка под требования бизнеса", "10-20 рабочих дней", 45000));
        services.add(new Service(4, 1, "Услуги","Техническая поддержка 1С (месяц)", "В день обращения", 8000));
        services.add(new Service(5, 1, "Услуги","Консультация по 1С", "1-2 рабочих дня", 3000));

        for (Service service : services) {
            ContentValues values = new ContentValues();
            values.put("id", service.getId());
            values.put("category_id", service.getCategory().getId());
            values.put("title", service.getTitle());
            values.put("deadline", service.getDeadline());
            values.put("price", service.getPrice());
            db.insert("services", null, values);
        }
        android.util.Log.d("DbHelper", "Services initialized: " + services.size());
    }

    /**
     * метод для получения всех услуг
     */
    public List<Service> getAllServices() {
        List<Service> serviceList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT s.*, c.title as category_title FROM services s " +
                "LEFT JOIN categories c ON s.category_id = c.id " +
                "ORDER BY s.id";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Service service = new Service(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(5),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                );
                serviceList.add(service);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return serviceList;
    }
}
