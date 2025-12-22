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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.firstbit_app.Adapters.CategoryAdapter;
import com.example.firstbit_app.Adapters.CombinedAdapter;
import com.example.firstbit_app.Adapters.ImageSliderAdapter;
import com.example.firstbit_app.Adapters.ProductAdapter;
import com.example.firstbit_app.Adapters.ServiceAdapter;
import com.example.firstbit_app.DbHelper;
import com.example.firstbit_app.Models.Category;
import com.example.firstbit_app.Models.Product;
import com.example.firstbit_app.Models.Service;
import com.example.firstbit_app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * главный экран приложения с товарами и услугами
 */
public class HomeActivity extends AppCompatActivity {

    private DbHelper dbHelper;
    private LinearLayout navHome, navCart, navProfile;
    RecyclerView categoryRecycler, productRecycler;
    CategoryAdapter categoryAdapter;
    private ViewPager2 viewPager2;
    private ImageSliderAdapter adapter;
    private ArrayList<Integer> imageList;
    private Handler handler;


    private List<Product> allProducts;
    private List<Service> allServices;
    private CombinedAdapter combinedAdapter;
    private ProductAdapter productAdapter;
    private ServiceAdapter serviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        int userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("user_id", -1);
        if (userId == -1) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        dbHelper = new DbHelper(this, null);

        productRecycler = findViewById(R.id.product_list);

        allProducts = dbHelper.getAllProducts();
        allServices = dbHelper.getAllServices();

        setupCombinedRecyclerView();

        viewPager2 = findViewById(R.id.image_slider);

        List<Category> categoryList = dbHelper.getAllCategories();
        setCategoryRecycler(categoryList);

        if (categoryAdapter != null) {
            categoryAdapter.setSelectedPosition(0);
        }
        filterProductsAndServicesByCategory(0);

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
        navProfile = findViewById(R.id.nav_profile);

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
     * настраивает горизонтальный список категорий
     */
    private void setCategoryRecycler(List<Category> categoryList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);

        categoryRecycler = findViewById(R.id.category_recycler);
        categoryRecycler.setLayoutManager(layoutManager);
        categoryRecycler.setNestedScrollingEnabled(false);

        categoryAdapter = new CategoryAdapter(this, categoryList);

        categoryAdapter.setOnCategoryClickListener(new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category, int position) {
                filterProductsAndServicesByCategory(category.getId());
            }
        });

        categoryRecycler.setAdapter(categoryAdapter);
    }

    /**
     * настраивает объединённый RecyclerView для отображения товаров/услуг в одной сетке
     */
    private void setupCombinedRecyclerView() {
        try {
            productAdapter = new ProductAdapter(this, allProducts);
            serviceAdapter = new ServiceAdapter(this, allServices);

            combinedAdapter = new CombinedAdapter(productAdapter, serviceAdapter);
            combinedAdapter.setData(allProducts, allServices);

            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
            productRecycler.setLayoutManager(layoutManager);
            productRecycler.setNestedScrollingEnabled(false);
            productRecycler.setAdapter(combinedAdapter);
        } catch (Exception e) {
            android.util.Log.e("HomeActivity", "Ошибка настройки комбинированного recycler view", e);
        }
    }

    /**
     * фильтрует товары и услуги по выбранной категории
     */
    private void filterProductsAndServicesByCategory(int categoryId) {
        List<Product> filteredProducts = new ArrayList<>();
        List<Service> filteredServices = new ArrayList<>();

        if (categoryId == 0) {
            filteredProducts.addAll(allProducts);
            filteredServices.addAll(allServices);
        } else if (categoryId == 1) {
            filteredProducts.clear();
            filteredServices.addAll(allServices);
        } else {
            for (Product product : allProducts) {
                if (product.getCategory().getId() == categoryId) {
                    filteredProducts.add(product);
                }
            }
            filteredServices.clear();
        }

        if (combinedAdapter != null) {
            combinedAdapter.setData(filteredProducts, filteredServices);
            combinedAdapter.notifyDataSetChanged();
        }
    }
}