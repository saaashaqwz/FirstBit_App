package com.example.firstbit_app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstbit_app.DbHelper;
import com.example.firstbit_app.Models.Product;
import com.example.firstbit_app.R;
import com.example.firstbit_app.UI.AuthActivity;

import java.util.List;

/**
 * адаптер для отображения списка товаров
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    List<Product> productList;
    private DbHelper dbHelper;
    private OnCartUpdateListener cartUpdateListener;

    public interface OnCartUpdateListener {
        void onCartItemAdded();
    }

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.dbHelper = new DbHelper(context, null);
    }

    /**
     * создаёт ViewHolder для товара
     */
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    /**
     * привязывает данные товара к ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productTitle.setText(product.getTitle());

        if (holder.productPrice != null) {
            holder.productPrice.setText(String.format("%,d ₽", product.getPrice()));
        }

        int resourceId = context.getResources().getIdentifier(
                product.getImage().replace(".png", ""),
                "drawable",
                context.getPackageName()
        );
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /**
     * вложанный класс
     */
    public static final class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle;
        TextView productPrice;
        Button addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            productPrice = itemView.findViewById(R.id.product_price);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }

    /**
     * закрывает подключение к БД при уничтожении
     */
    @Override
    protected void finalize() throws Throwable {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.finalize();
    }
}