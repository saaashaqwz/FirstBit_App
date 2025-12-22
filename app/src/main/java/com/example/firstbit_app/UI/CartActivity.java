package com.example.firstbit_app.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstbit_app.Adapters.CartAdapter;
import com.example.firstbit_app.DbHelper;
import com.example.firstbit_app.Models.Cart;
import com.example.firstbit_app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * активность корзины - отображает товары и услуги, добавленные пользователем
 */
public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartUpdateListener {

    private LinearLayout navHome, navCart, navProfile;
    private DbHelper dbHelper;
    private RecyclerView cartRecyclerView;
    private TextView emptyCartText, totalPriceText, btnCheckout;
    private LinearLayout checkoutSection;
    private CartAdapter cartAdapter;
    private boolean isUpdating = false;
    private List<Cart> carts;

    /**
     * вызывается при первом создании активности
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        dbHelper = new DbHelper(this, null);

        initializeViews();
        setupCustomNavigation();
        setupCartRecyclerView();
        setActiveNavItem(navCart);
        loadCartItems();
    }

    /**
     * инициализирует все View-элементы интерфейса
     */
    private void initializeViews() {
        navHome = findViewById(R.id.nav_home);
        navCart = findViewById(R.id.nav_cart);
        navProfile = findViewById(R.id.nav_profile);

        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        emptyCartText = findViewById(R.id.empty_cart_text);
        totalPriceText = findViewById(R.id.total_price_text);
        btnCheckout = findViewById(R.id.btn_checkout);
        checkoutSection = findViewById(R.id.bottom_section);

        btnCheckout.setOnClickListener(v -> checkoutCart());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setActiveNavItem(navCart);
    }

    /**
     * настраивает обработчики кликов для нижней навигации
     */
    private void setupCustomNavigation() {
        navHome.setOnClickListener(v -> handleNavigationClick(v, HomeActivity.class));
        navCart.setOnClickListener(v -> handleNavigationClick(v, CartActivity.class));
        navProfile.setOnClickListener(v -> handleNavigationClick(v, ProfileActivity.class));
    }

    /**
     * обрабатывает переход по нижней навигации
     */
    private void handleNavigationClick(View navView, Class<?> targetActivity) {
        if (this.getClass().equals(targetActivity)) {
            setActiveNavItem((LinearLayout) navView);
            return;
        }

        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        finish();
    }

    /**
     * выделяет активный пункт нижней навигации визуально
     */
    private void setActiveNavItem(LinearLayout activeNav) {
        resetAllNavItems();

        activeNav.setSelected(true);

        ImageView icon = (ImageView) activeNav.getChildAt(0);
        TextView text = (TextView) activeNav.getChildAt(1);

        icon.setColorFilter(ContextCompat.getColor(this, R.color.nav_item_active));
        text.setTextColor(ContextCompat.getColor(this, R.color.nav_item_active));
        text.setTextSize(13);
    }

    /**
     * сбрасывает визуальное выделение всех пунктов нижней навигации
     */
    private void resetAllNavItems() {
        LinearLayout[] navItems = {navHome, navCart, navProfile};
        int inactiveColor = ContextCompat.getColor(this, R.color.nav_item_inactive);

        for (LinearLayout navItem : navItems) {
            navItem.setSelected(false);

            ImageView icon = (ImageView) navItem.getChildAt(0);
            TextView text = (TextView) navItem.getChildAt(1);

            icon.setColorFilter(inactiveColor);
            text.setTextColor(inactiveColor);
            text.setTextSize(12);
        }
    }

    /**
     * настраивает RecyclerView с элементами корзины
     */
    private void setupCartRecyclerView() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Сначала войдите в аккаунт", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        carts = dbHelper.getCartItems(userId);

        cartAdapter = new CartAdapter(this, carts, this);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);
    }

    /**
     * обновляет видимость блока "пустая корзина"
     */
    private void updateEmptyState() {
        if (cartAdapter == null || cartAdapter.getItemCount() == 0) {
            cartRecyclerView.setVisibility(View.GONE);
            emptyCartText.setVisibility(View.VISIBLE);
            checkoutSection.setVisibility(View.GONE);
        } else {
            cartRecyclerView.setVisibility(View.VISIBLE);
            emptyCartText.setVisibility(View.GONE);
            checkoutSection.setVisibility(View.VISIBLE);
        }
    }

    /**
     * обновляет отображение общей суммы корзины
     */
    private void updateCartTotal() {
        int userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getInt("user_id", -1);

        if (userId == -1) {
            return;
        }

        int total = dbHelper.getCartTotalPrice(userId);

        if (totalPriceText != null) {
            totalPriceText.setText(String.format("Итого: %,d ₽", total));
        }
    }

    /**
     * закрывает подключение к БД при уничтожении активности
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    /**
     * вызывается при изменении содержимого корзины
     */
    @Override
    public void onCartUpdated() {
        if (!isUpdating) {
            isUpdating = true;

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                updateCartTotal();
                updateEmptyState();
                isUpdating = false;
            }, 200);
        }
    }

    /**
     * загружает элементы корзины и обновляет UI
     */
    private void loadCartItems() {
        int userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getInt("user_id", -1);

        if (userId == -1) return;

        carts = dbHelper.getCartItems(userId);
        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
        }
        updateCartTotal();
        updateEmptyState();
    }

    /**
     * оформляет заказ из корзины
     */
    private void checkoutCart() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Cart> cartItems = dbHelper.getCartItems(userId);

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
            return;
        }

        int orderId = dbHelper.createOrder(userId, cartItems);

        if (orderId != -1) {
            for (Cart item : cartItems) {
                dbHelper.removeFromCart(item.getId());
            }

            loadCartItems();
            showSuccessDialog(orderId);

        } else {
            Toast.makeText(this, "Ошибка при оформлении заказа", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * показывает окно успеха с номером заказа
     */
    private void showSuccessDialog(int orderId) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);

        builder.setTitle("Заказ успешно оформлен!")
                .setMessage("Ваш заказ №" + orderId + " принят в обработку.\n\nМы свяжемся с вами в ближайшее время.")
                .setIcon(R.drawable.icon_success)
                .setPositiveButton("Отлично!", (dialog, which) -> dialog.dismiss())
                .setCancelable(false);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setTextColor(getResources().getColor(R.color.magenta));
    }
}