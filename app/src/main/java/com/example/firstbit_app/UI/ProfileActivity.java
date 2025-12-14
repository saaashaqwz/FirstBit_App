package com.example.firstbit_app.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstbit_app.Adapters.OrderAdapter;
import com.example.firstbit_app.Adapters.OrderDetailAdapter;
import com.example.firstbit_app.DbHelper;
import com.example.firstbit_app.Models.Order;
import com.example.firstbit_app.Models.OrderItem;
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
            String name = dbHelper.getUserNameById(userId);
            String login = dbHelper.getUserLoginById(userId);

            if (name != null && !name.trim().isEmpty()) {
                userGreeting.setText("Привет, " + name.trim() + "!");
            } else if (login != null && !login.trim().isEmpty()) {
                userGreeting.setText("Привет, " + login + "!");
            } else {
                userGreeting.setText("Привет, Пользователь!");
            }
        }

        ImageView btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            prefs.edit().remove("user_id").apply();

            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        });

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

        if (!orders.isEmpty()) {
            orderAdapter = new OrderAdapter(this, orders);
            ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ordersRecyclerView.setAdapter(orderAdapter);

            orderAdapter.setOnOrderClickListener(order -> {
                showOrderDetailDialog(order.getId(), order.getTotal());
            });
        }
    }

    private void showOrderDetailDialog(int orderId, int orderTotal) {
        List<OrderItem> orderItems = dbHelper.getOrderItems(orderId);

        if (orderItems.isEmpty()) {
            Toast.makeText(this, "Нет данных по заказу", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Заказ №" + orderId);

        View view = getLayoutInflater().inflate(R.layout.dialog_order_detail, null);
        RecyclerView recyclerView = view.findViewById(R.id.order_items_recycler);
        TextView totalText = view.findViewById(R.id.order_total_text);
        Button btnClose = view.findViewById(R.id.btn_close_dialog);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new OrderDetailAdapter(this, orderItems));

        totalText.setText(String.format("Итого: %,d ₽", orderTotal));

        builder.setView(view);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}