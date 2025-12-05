package com.example.firstbit_app.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.firstbit_app.DbHelper;
import com.example.firstbit_app.Models.Product;
import com.example.firstbit_app.Models.Service;
import com.example.firstbit_app.R;

/**
 * активность детальной карточки товара/услуги
 */
public class ItemDetailActivity extends AppCompatActivity {

    private ImageView itemImage;
    private TextView itemTitle, itemPrice, itemDescription, itemLicense, itemDeadline;
    private Button addToCartButton;
    private Toolbar toolbar;
    private DbHelper dbHelper;
    private String type;
    private int id;
    private int userId;

    /**
     * вызывается при первом создании активности
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        dbHelper = new DbHelper(this, null);

        toolbar = findViewById(R.id.toolbar_detail);

        itemImage = findViewById(R.id.item_image);
        itemTitle = findViewById(R.id.item_title);
        itemPrice = findViewById(R.id.item_price);
        itemDescription = findViewById(R.id.item_description);
        itemLicense = findViewById(R.id.item_license);
        itemDeadline = findViewById(R.id.item_deadline);
        addToCartButton = findViewById(R.id.add_to_cart_button);

        type = getIntent().getStringExtra("type");
        id = getIntent().getIntExtra("id", -1);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (id == -1 || type == null) {
            Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadItemDetails();

        addToCartButton.setOnClickListener(v -> addToCart());
    }

    private void loadItemDetails() {
        if (type.equals("product")) {
            Product product = dbHelper.getProductId(id);
            if (product != null) {
                setTitle(product.getTitle());

                itemTitle.setText(product.getTitle());
                itemPrice.setText(String.format("%,d ₽", product.getPrice()));

                itemDescription.setText(product.getDescription());
                itemDescription.setVisibility(View.VISIBLE);

                itemLicense.setText(product.getLicense());
                itemLicense.setVisibility(View.VISIBLE);

                itemDeadline.setVisibility(View.GONE);

                int resourceId = getResources().getIdentifier(
                        product.getImage().replace(".png", "").replace(".jpg", ""),
                        "drawable",
                        getPackageName()
                );
                itemImage.setImageResource(resourceId != 0 ? resourceId : R.drawable.product_placeholder);

                addToCartButton.setText("Добавить в корзину");
            }
        }
        else if (type.equals("service")) {
            Service service = dbHelper.getServiceId(id);
            if (service != null) {
                setTitle(service.getTitle());

                itemTitle.setText(service.getTitle());
                itemPrice.setText(String.format("%,d ₽", service.getPrice()));

                itemDeadline.setText("Срок: " + service.getDeadline());
                itemDeadline.setVisibility(View.VISIBLE);

                itemDescription.setVisibility(View.GONE);
                itemLicense.setVisibility(View.GONE);

                itemImage.setImageResource(R.drawable.icon_service);

                addToCartButton.setText("Заказать");
            }
        }
    }

    /**
     * добавление товара в корзину
     */
    private void addToCart() {
        if (userId == -1) {
            Toast.makeText(this, "Сначала войдите в аккаунт", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthActivity.class));
            return;
        }

        int productId = type.equals("product") ? id : 0;
        int serviceId = type.equals("service") ? id : 0;

        boolean success = dbHelper.addToCart(userId, productId, serviceId);
        if (success) {
            Toast.makeText(this, "Добавлено в корзину", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка добавления в корзину", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}