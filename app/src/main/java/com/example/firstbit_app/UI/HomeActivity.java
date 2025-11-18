package com.example.firstbit_app.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.firstbit_app.Adapters.ImageSliderAdapter;
import com.example.firstbit_app.R;

import java.util.ArrayList;

/**
 * главный экран приложения с товарами и услугами
 */
public class HomeActivity extends AppCompatActivity {

    private LinearLayout navHome, navCart, navSetting;
    private ViewPager2 viewPager2;
    private ImageSliderAdapter adapter;
    private ArrayList<Integer> imageList;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewPager2 = findViewById(R.id.image_slider);

        initializeViews();
        setupCustomNavigation();

        setActiveNavItem(navHome);

        setTransfarmer();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (handler != null) {
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 5000);
                }
            }
        });
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager2 != null) {
                int currentItem = viewPager2.getCurrentItem();
                int totalItems = adapter != null ? adapter.getItemCount() : 0;
                if (totalItems > 0) {
                    viewPager2.setCurrentItem((currentItem + 1) % totalItems);
                }
            }
        }
    };

    private void setTransfarmer() {
        if (viewPager2 == null) return;

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(10));

        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.14f);
        });

        viewPager2.setPageTransformer(transformer);
    }

    private void initializeViews() {
        navHome = findViewById(R.id.nav_home);
        navCart = findViewById(R.id.nav_cart);
        navSetting = findViewById(R.id.nav_setting);

        imageList = new ArrayList<>();

        imageList.add(R.drawable.banner_1);
        imageList.add(R.drawable.banner_2);
        imageList.add(R.drawable.banner_3);
        imageList.add(R.drawable.banner_4);
        imageList.add(R.drawable.banner_5);

        adapter = new ImageSliderAdapter(this, imageList, viewPager2);
        handler = new Handler(Looper.myLooper());

        if (viewPager2 != null) {
            viewPager2.setAdapter(adapter);
            viewPager2.setOffscreenPageLimit(3);
            viewPager2.setClipToPadding(false);
            viewPager2.setClipChildren(false);

            if (viewPager2.getChildAt(0) instanceof RecyclerView) {
                viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
            }
        }
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