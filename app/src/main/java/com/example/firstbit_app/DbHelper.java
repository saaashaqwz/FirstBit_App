package com.example.firstbit_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.firstbit_app.Models.Cart;
import com.example.firstbit_app.Models.Category;
import com.example.firstbit_app.Models.Order;
import com.example.firstbit_app.Models.OrderItem;
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
    private static final int DATABASE_VERSION = 8;

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
                "name TEXT DEFAULT '', " +
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

        // таблица заказов
        String CREATE_ORDERS_TABLE = "CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "status TEXT, " +
                "deadline TEXT, " +
                "total INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(CREATE_ORDERS_TABLE);
        android.util.Log.d("DbHelper", "Таблица заказов создана");

        // таблица элементов заказа
        String CREATE_ORDER_ITEMS_TABLE = "CREATE TABLE order_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER, " +
                "product_id INTEGER, " +
                "service_id INTEGER, " +
                "title TEXT, " +
                "price INTEGER, " +
                "quantity INTEGER DEFAULT 1, " +
                "deadline TEXT, " +
                "type TEXT, " +  // "product" or "service"
                "FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(product_id) REFERENCES products(id), " +
                "FOREIGN KEY(service_id) REFERENCES services(id))";
        db.execSQL(CREATE_ORDER_ITEMS_TABLE);
        android.util.Log.d("DbHelper", "Таблица элементов заказов создана");

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
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS order_items");

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

    public String getUserLoginById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT login FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        String login = null;
        if (cursor.moveToFirst()) {
            login = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return login;
    }

    public String getUserNameById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM users WHERE id = ?",
                new String[]{String.valueOf(userId)});
        String name = "";
        if (c.moveToFirst()) {
            name = c.getString(0);
            if (name == null || name.trim().isEmpty()) {
                name = "";
            }
        }
        c.close();
        db.close();
        return name;
    }

    public String getUserPhoneById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT phone FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        String phone = "";
        if (c.moveToFirst()) phone = c.getString(0);
        c.close();
        db.close();
        return phone;
    }

    public boolean updateUserNameAndPhone(int userId, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("phone", phone);
        int rows = db.update("users", cv, "id = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    /**
     * проверка пользователя по телефону и паролю
     */
    public boolean getUserByPhone(String phone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE phone = ? AND password = ?",
                new String[]{phone, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    /**
     * получение ID пользователя по телефону
     */
    public int getUserIdByPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE phone = ?", new String[]{phone});
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
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

    /**
     * возвращает продукт по его id
     */
    public Product getProductId(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Product product = null;

        String query = "SELECT p.id, p.image, p.category_id, c.title AS category_title, " +
                "p.title, p.description, p.license, p.price " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "WHERE p.id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId)});

        if (cursor != null && cursor.moveToFirst()) {
            product = new Product(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getInt(7)
            );
            cursor.close();
        }

        db.close();
        return product;
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

    /**
     * возвращает услугу по её id
     */
    public Service getServiceId(int serviceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Service service = null;

        String query = "SELECT s.id, s.category_id, c.title AS category_title, " +
                "s.title, s.deadline, s.price " +
                "FROM services s " +
                "LEFT JOIN categories c ON s.category_id = c.id " +
                "WHERE s.id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(serviceId)});

        if (cursor != null && cursor.moveToFirst()) {
            service = new Service(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5)
            );
            cursor.close();
        }

        db.close();
        return service;
    }

    /**
     * добавление товара в корзину
     */
    public boolean addToCart(int userId, int productId, int serviceId) {
        if (isItemInCart(userId, productId, serviceId)) {
            CartQuantityInfo info = getCartItemQuantityInfo(userId, productId, serviceId);
            int newQuantity = info.quantity + 1;
            return updateCartItemQuantity(info.cartItemId, newQuantity);
        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_id", userId);

            if (productId > 0) {
                values.put("product_id", productId);
            }
            if (serviceId > 0) {
                values.put("service_id", serviceId);
            }

            long result = db.insert("cart", null, values);
            db.close();
            return result != -1;
        }
    }

    /**
     * получение всех элементов корзины для пользователя
     */
    public List<Cart> getCartItems(int userId) {
        List<Cart> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT c.*, " +
                "p.title as product_title, p.price as product_price, p.image as product_image, " +
                "s.title as service_title, s.price as service_price, s.deadline as service_deadline " +
                "FROM cart c " +
                "LEFT JOIN products p ON c.product_id = p.id " +
                "LEFT JOIN services s ON c.service_id = s.id " +
                "WHERE c.user_id = ? " +
                "ORDER BY c.added_date DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Cart item = new Cart();
                item.setId(cursor.getInt(0));
                item.setUserId(cursor.getInt(1));

                int productId = cursor.getInt(2);
                int serviceId = cursor.getInt(3);
                int quantity = cursor.getInt(4);

                if (!cursor.isNull(2)) {
                    item.setProductId(productId);
                    item.setTitle(cursor.getString(6));
                    int price = cursor.getInt(7);
                    item.setPrice(price);
                    item.setImage(cursor.getString(8));
                    item.setType("product");
                } else if (!cursor.isNull(3)) {
                    item.setServiceId(serviceId);
                    item.setTitle(cursor.getString(9));
                    int price = cursor.getInt(10);
                    item.setPrice(price);
                    item.setDeadline(cursor.getString(11));
                    item.setType("service");
                }

                item.setQuantity(quantity);
                item.setAddedDate(cursor.getString(5));

                cartItems.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cartItems;
    }

    /**
     * обновление количества товара в корзине
     */
    public boolean updateCartItemQuantity(int cartItemId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);

        int result = db.update("cart", values, "id = ?",
                new String[]{String.valueOf(cartItemId)});
        db.close();
        return result > 0;
    }

    /**
     * удаление элемента из корзины
     */
    public boolean removeFromCart(int cartItemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("cart", "id = ?",
                new String[]{String.valueOf(cartItemId)});
        db.close();
        return result > 0;
    }

    /**
     * получение общего количества товаров в корзине
     */
    public int getCartItemsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(quantity) FROM cart WHERE user_id = ?",
                new String[]{String.valueOf(userId)});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    /**
     * получение общей суммы корзины
     */
    public int getCartTotalPrice(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT SUM(CASE " +
                "WHEN c.product_id IS NOT NULL THEN p.price * c.quantity " +
                "WHEN c.service_id IS NOT NULL THEN s.price * c.quantity " +
                "ELSE 0 END) as total " +
                "FROM cart c " +
                "LEFT JOIN products p ON c.product_id = p.id " +
                "LEFT JOIN services s ON c.service_id = s.id " +
                "WHERE c.user_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return total;
    }

    /**
     * проверяет, есть ли товар/услуга уже в корзине пользователя
     */
    public boolean isItemInCart(int userId, int productId, int serviceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        String[] args;

        if (productId > 0) {
            query = "SELECT * FROM cart WHERE user_id = ? AND product_id = ?";
            args = new String[]{String.valueOf(userId), String.valueOf(productId)};
        } else {
            query = "SELECT * FROM cart WHERE user_id = ? AND service_id = ?";
            args = new String[]{String.valueOf(userId), String.valueOf(serviceId)};
        }

        Cursor cursor = db.rawQuery(query, args);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    /**
     * получает текущее количество и ID записи для товара/услуги в корзине
     */
    public CartQuantityInfo getCartItemQuantityInfo(int userId, int productId, int serviceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        String[] args;

        if (productId > 0) {
            query = "SELECT id, quantity FROM cart WHERE user_id = ? AND product_id = ?";
            args = new String[]{String.valueOf(userId), String.valueOf(productId)};
        } else {
            query = "SELECT id, quantity FROM cart WHERE user_id = ? AND service_id = ?";
            args = new String[]{String.valueOf(userId), String.valueOf(serviceId)};
        }

        Cursor cursor = db.rawQuery(query, args);
        CartQuantityInfo info = new CartQuantityInfo();

        if (cursor.moveToFirst()) {
            info.cartItemId = cursor.getInt(0);
            info.quantity = cursor.getInt(1);
        }

        cursor.close();
        db.close();
        return info;
    }

    /**
     * вспомогательный класс для хранения информации о количестве
     */
    public static class CartQuantityInfo {
        public int cartItemId;
        public int quantity;

        public CartQuantityInfo() {
            this.cartItemId = -1;
            this.quantity = 0;
        }
    }

    /**
     * метод для получения одного элемента корзины по ID
     */
    public Cart getCartItem(int cartId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT c.*, " +
                "p.title as product_title, p.price as product_price, p.image as product_image, " +
                "s.title as service_title, s.price as service_price, s.deadline as service_deadline " +
                "FROM cart c " +
                "LEFT JOIN products p ON c.product_id = p.id " +
                "LEFT JOIN services s ON c.service_id = s.id " +
                "WHERE c.id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(cartId)});

        Cart item = null;
        if (cursor.moveToFirst()) {
            item = new Cart();
            item.setId(cursor.getInt(0));
            item.setUserId(cursor.getInt(1));

            int productId = cursor.getInt(2);
            int serviceId = cursor.getInt(3);
            int quantity = cursor.getInt(4);

            if (!cursor.isNull(2)) {
                item.setProductId(productId);
                item.setTitle(cursor.getString(6));
                int price = cursor.getInt(7);
                item.setPrice(price);
                item.setImage(cursor.getString(8));
                item.setType("product");
            } else if (!cursor.isNull(3)) {
                item.setServiceId(serviceId);
                item.setTitle(cursor.getString(9));
                int price = cursor.getInt(10);
                item.setPrice(price);
                item.setDeadline(cursor.getString(11));
                item.setType("service");
            }

            item.setQuantity(quantity);
            item.setAddedDate(cursor.getString(5));
        }

        cursor.close();
        db.close();
        return item;
    }

    /**
     * создаёт заказ на основе всей корзины и возвращает ID нового заказа
     */
    public int createOrder(int userId, List<Cart> cartItems) {
        if (cartItems.isEmpty()) return -1;

        int total = 0;
        String deadline = "Н/Д";
        boolean hasService = false;
        String serviceDeadline = null;

        for (Cart item : cartItems) {
            total += item.getTotalPrice();
            if (item.getType().equals("service")) {
                hasService = true;
                if (serviceDeadline == null) {
                    serviceDeadline = item.getDeadline();
                }
            }
        }

        if (hasService) {
            deadline = serviceDeadline;
        }

        String status = "В обработке";

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("status", status);
        values.put("deadline", deadline);
        values.put("total", total);

        long newRowId = db.insert("orders", null, values);

        if (newRowId == -1) {
            db.close();
            return -1;
        }

        for (Cart item : cartItems) {
            ContentValues itemValues = new ContentValues();
            itemValues.put("order_id", newRowId);
            itemValues.put("title", item.getTitle());
            itemValues.put("price", item.getPrice());
            itemValues.put("quantity", item.getQuantity());
            itemValues.put("deadline", item.getDeadline());
            itemValues.put("type", item.getType());

            if (item.getType().equals("product")) {
                itemValues.put("product_id", item.getProductId());
            } else if (item.getType().equals("service")) {
                itemValues.put("service_id", item.getServiceId());
            }

            db.insert("order_items", null, itemValues);
        }

        db.close();
        return (int) newRowId;
    }

    public List<Order> getUserOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT o.id, o.user_id, o.status, o.deadline, o.total " +
                "FROM orders o " +
                "WHERE o.user_id = ? " +
                "ORDER BY o.id DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                );
                orders.add(order);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return orders;
    }

    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT title, price, quantity, deadline, type FROM order_items WHERE order_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});

        if (cursor.moveToFirst()) {
            do {
                OrderItem item = new OrderItem(
                        cursor.getString(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }
}
