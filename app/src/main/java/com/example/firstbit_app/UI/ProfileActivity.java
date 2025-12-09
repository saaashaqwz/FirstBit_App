package com.example.firstbit_app.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstbit_app.Adapters.OrderAdapter;
import com.example.firstbit_app.DbHelper;
import com.example.firstbit_app.Models.Order;
import com.example.firstbit_app.R;

import java.util.List;

/**
 * активность профиля пользователя - отображает его данные и заказы
 */
public class ProfileActivity extends AppCompatActivity {

    private LinearLayout navHome, navCart, navProfile;
    private ImageView btnSettings;

    private TextView userGreeting, emptyOrdersText;
    private RecyclerView ordersRecyclerView;
    private DbHelper dbHelper;
    private OrderAdapter orderAdapter;
    private int userId = -1;
    private String userLogin = "Гость";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DbHelper(this, null);

        userGreeting = findViewById(R.id.tv_user_name);
        ordersRecyclerView = findViewById(R.id.rv_orders);
        emptyOrdersText = findViewById(R.id.empty_orders);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            userLogin = dbHelper.getUserLoginById(userId); // Добавим этот метод в DbHelper
            if (userLogin == null) userLogin = "Пользователь";
        }

        userGreeting.setText("Привет, " + userLogin + "!");

        setupOrders();
        initializeViews();
        setupCustomNavigation();
        setActiveNavItem(navProfile);
        setupSettingsButton();
    }

    private void initializeViews() {
        navHome = findViewById(R.id.nav_home);
        navCart = findViewById(R.id.nav_cart);
        navProfile = findViewById(R.id.nav_profile);
        btnSettings = findViewById(R.id.btn_settings);
    }

    private void setupSettingsButton() {
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupOrders();
        setActiveNavItem(navProfile);
    }

    private void setupCustomNavigation() {
        navHome.setOnClickListener(v -> handleNavigationClick(v, HomeActivity.class));
        navCart.setOnClickListener(v -> handleNavigationClick(v, CartActivity.class));
        navProfile.setOnClickListener(v -> handleNavigationClick(v, ProfileActivity.class));
    }

    private void handleNavigationClick(View navView, Class<?> targetActivity) {
        if (this.getClass().equals(targetActivity)) {
            setActiveNavItem((LinearLayout) navView);
            return;
        }

        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        finish();
    }

    private void setActiveNavItem(LinearLayout activeNav) {
        resetAllNavItems();

        activeNav.setSelected(true);

        ImageView icon = (ImageView) activeNav.getChildAt(0);
        TextView text = (TextView) activeNav.getChildAt(1);

        icon.setColorFilter(ContextCompat.getColor(this, R.color.nav_item_active));
        text.setTextColor(ContextCompat.getColor(this, R.color.nav_item_active));
        text.setTextSize(13);
    }

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

    private void setupOrders() {
        if (userId == -1) {
            emptyOrdersText.setText("Войдите в аккаунт, чтобы видеть заказы");
            emptyOrdersText.setVisibility(View.VISIBLE);
            return;
        }

        List<Order> orders = dbHelper.getUserOrders(userId);

        if (orders.isEmpty()) {
            emptyOrdersText.setVisibility(View.VISIBLE);
            ordersRecyclerView.setVisibility(View.GONE);
        } else {
            emptyOrdersText.setVisibility(View.GONE);
            ordersRecyclerView.setVisibility(View.VISIBLE);

            orderAdapter = new OrderAdapter(this, orders);
            ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ordersRecyclerView.setAdapter(orderAdapter);
        }
    }
}