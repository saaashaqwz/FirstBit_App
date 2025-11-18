package com.example.firstbit_app.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.firstbit_app.R;

/**
 * активность настройки - отображает данные пользователя
 */
public class SettingActivity extends AppCompatActivity {

    private LinearLayout navHome, navCart, navSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initializeViews();
        setupCustomNavigation();
        setActiveNavItem(navSetting);
    }

    private void initializeViews() {
        navHome = findViewById(R.id.nav_home);
        navCart = findViewById(R.id.nav_cart);
        navSetting = findViewById(R.id.nav_setting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setActiveNavItem(navSetting);
    }

    private void setupCustomNavigation() {
        navHome.setOnClickListener(v -> handleNavigationClick(v, HomeActivity.class));
        navCart.setOnClickListener(v -> handleNavigationClick(v, CartActivity.class));
        navSetting.setOnClickListener(v -> handleNavigationClick(v, SettingActivity.class));
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