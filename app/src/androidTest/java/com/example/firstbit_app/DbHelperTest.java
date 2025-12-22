package com.example.firstbit_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import static org.junit.Assert.*;

import com.example.firstbit_app.Models.Cart;

@RunWith(AndroidJUnit4.class)
public class DbHelperTest {

    private DbHelper db;
    private Context context;
    private static final int TEST_USER_ID = 1;
    private static final int TEST_PRODUCT_ID = 1; // Бухгалтерия БАЗОВАЯ
    private static final int TEST_SERVICE_ID = 1; // Внедрение 1С:Бухгалтерия

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();

        db = new DbHelper(context, null);

        createTestUser();
    }

    @After
    public void tearDown() {
        if (db != null) {
            clearTestData();
            db.close();
        }
    }

    private void createTestUser() {
        SQLiteDatabase database = db.getWritableDatabase();
        try {
            database.delete("users", "login = ?", new String[]{"testuser"});

            android.content.ContentValues values = new android.content.ContentValues();
            values.put("id", TEST_USER_ID);
            values.put("login", "testuser");
            values.put("name", "Test User");
            values.put("phone", "+79991234567");
            values.put("password", "testpass");
            database.insert("users", null, values);
        } finally {
            database.close();
        }
    }

    private void clearTestData() {
        SQLiteDatabase database = db.getWritableDatabase();
        try {
            database.delete("cart", null, null);
            database.delete("order_items", null, null);
            database.delete("orders", null, null);
        } finally {
            database.close();
        }
    }

    @Test
    public void testAddToCart() {
        int userId = TEST_USER_ID;
        int productId = TEST_PRODUCT_ID;

        boolean firstAddition = db.addToCart(userId, productId, 0);
        assertTrue("Первый товар должен успешно добавиться в корзину", firstAddition);

        int itemsCount = db.getCartItemsCount(userId);
        assertEquals("После добавления одного товара количество должно быть 1",
                1, itemsCount);

        for (int i = 0; i < 4; i++) {
            boolean additionalAddition = db.addToCart(userId, productId, 0);
            assertTrue("Добавление в пределах лимита должно быть успешно (попытка " + (i+1) + ")",
                    additionalAddition);
        }

        int currentCount = db.getCartItemsCount(userId);
        assertEquals("После 5 добавлений количество должно быть 5", 5, currentCount);

        boolean sixthAddition = db.addToCart(userId, productId, 0);
        assertFalse("Шестая попытка добавления должна вернуть false (лимит 5 на товар)",
                sixthAddition);

        int finalCount = db.getCartItemsCount(userId);
        assertEquals("После попытки превысить лимит количество должно остаться 5",
                5, finalCount);

        int serviceId = TEST_SERVICE_ID;
        boolean serviceAddition = db.addToCart(userId, 0, serviceId);
        assertTrue("Услуга должна успешно добавиться", serviceAddition);

        int countWithService = db.getCartItemsCount(userId);
        assertEquals("После добавления услуги общее количество должно быть 6",
                6, countWithService);
    }

    @Test
    public void testCartLimits() {
        int userId = TEST_USER_ID;

        for (int i = 1; i <= 50; i++) {
            int productId = i;
            boolean added = db.addToCart(userId, productId, 0);
            assertTrue("Добавление товара " + i + " должно быть успешным", added);
        }

        int totalCount = db.getCartItemsCount(userId);
        assertEquals("Общее количество должно быть 50", 50, totalCount);

        boolean extraAddition = db.addToCart(userId, 51, 0);
        assertFalse("Добавление 51-го товара должно вернуть false (лимит 50)",
                extraAddition);

        int finalCount = db.getCartItemsCount(userId);
        assertEquals("Количество должно остаться 50 после попытки превысить лимит",
                50, finalCount);
    }

    @Test
    public void testRemoveFromCart() {
        int userId = TEST_USER_ID;

        db.addToCart(userId, TEST_PRODUCT_ID, 0);

        List<Cart> cartItems = db.getCartItems(userId);
        assertFalse(cartItems.isEmpty());
        int itemId = cartItems.get(0).getId();

        int initialCount = db.getCartItemsCount(userId);
        assertEquals(1, initialCount);

        boolean removed = db.removeFromCart(itemId);
        assertTrue("Удаление должно быть успешным", removed);

        int finalCount = db.getCartItemsCount(userId);
        assertEquals("Количество должно быть 0 после удаления", 0, finalCount);

        List<Cart> finalItems = db.getCartItems(userId);
        assertTrue("Список должен быть пустым после удаления", finalItems.isEmpty());
    }

    @Test
    public void testGetCartTotalPrice() {
        int userId = TEST_USER_ID;

        db.addToCart(userId, TEST_PRODUCT_ID, 0);

        db.addToCart(userId, 0, TEST_SERVICE_ID);

        int total = db.getCartTotalPrice(userId);
        assertEquals("Общая сумма должна быть 29400 (4400 + 25000)", 29400, total);

        List<Cart> items = db.getCartItems(userId);
        for (Cart item : items) {
            if (item.getProductId() == TEST_PRODUCT_ID) {
                db.updateCartItemQuantity(item.getId(), 2);
                break;
            }
        }

        int updatedTotal = db.getCartTotalPrice(userId);
        assertEquals("Обновленная сумма должна быть 33800 (4400*2 + 25000)", 33800, updatedTotal);
    }
}
