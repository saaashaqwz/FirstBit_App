package com.example.firstbit_app.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.firstbit_app.R;

/**
 * главный экран приложения с товарами и услугами
 */
public class HomeActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private LinearLayout navHome, navCart, navSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
        setupCustomNavigation();

        setActiveNavItem(navHome);
    }

    private void initializeViews() {
        tvGreeting = findViewById(R.id.tv_greeting);
        navHome = findViewById(R.id.nav_home);
        navCart = findViewById(R.id.nav_cart);
        navSetting = findViewById(R.id.nav_setting);
    }

    private void setupCustomNavigation() {
        navHome.setOnClickListener(v -> handleNavigationClick(v, "Добро пожаловать!"));
        navCart.setOnClickListener(v -> handleNavigationClick(v, "Ваша корзина "));
        navSetting.setOnClickListener(v -> handleNavigationClick(v, "Ваши настройки"));
    }

    private void handleNavigationClick(View navView, String greeting) {
        tvGreeting.setText(greeting);

        setActiveNavItem((LinearLayout) navView);
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
        LinearLayout[] navItems = {navHome, navCart, navSetting};
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
}