package com.example.firstbit_app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
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
import com.example.firstbit_app.Models.Service;
import com.example.firstbit_app.R;
import com.example.firstbit_app.UI.AuthActivity;
import com.example.firstbit_app.UI.ItemDetailActivity;

import java.util.List;

/**
 * адаптер для отображения списка услуг
 */
public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private Context context;
    List<Service> serviceList;
    private DbHelper dbHelper;
    private OnCartUpdateListener cartUpdateListener;

    public interface OnCartUpdateListener {
        void onCartItemAdded();
    }

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
        this.dbHelper = new DbHelper(context, null);
    }

    /**
     * создаёт ViewHolder для услуги
     */
    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_item, parent, false);
        return new ServiceViewHolder(view);
    }

    /**
     * привязывает данные услуги к ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);

        if (holder.serviceTitle != null) {
            holder.serviceTitle.setText(service.getTitle());
        } else {
            Log.e("ServiceAdapter", "serviceTitle имеет значение null в позиции: " + position);
        }

        if (holder.serviceDeadline != null) {
            holder.serviceDeadline.setText("Срок: " + service.getDeadline());
        } else {
            Log.e("ServiceAdapter", "serviceDeadline имеет значение null в позиции: " + position);
        }

        if (holder.servicePrice != null) {
            holder.servicePrice.setText(String.format("%,d ₽", service.getPrice()));
        }

        if (holder.serviceIcon != null) {
            holder.serviceIcon.setImageResource(R.drawable.icon_service);
        }

        if (holder.addToCartButton != null) {
            holder.addToCartButton.setOnClickListener(v -> {
                addServiceToCart(service);
            });
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("type", "service");
            intent.putExtra("id", service.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList != null ? serviceList.size() : 0;
    }

    /**
     * вложанный класс
     */
    public static final class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceIcon;
        TextView serviceTitle, serviceDeadline, servicePrice;
        Button addToCartButton;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceIcon = itemView.findViewById(R.id.service_icon);
            serviceTitle = itemView.findViewById(R.id.service_title);
            serviceDeadline = itemView.findViewById(R.id.service_deadline);
            servicePrice = itemView.findViewById(R.id.service_price);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);

            if (serviceIcon == null) Log.e("ServiceViewHolder", "serviceIcon не найден");
            if (serviceTitle == null) Log.e("ServiceViewHolder", "serviceTitle не найден");
            if (serviceDeadline == null) Log.e("ServiceViewHolder", "serviceDeadline не найден");
            if (servicePrice == null) Log.e("ServiceViewHolder", "servicePrice не найден");
            if (addToCartButton == null) Log.e("ServiceViewHolder", "addToCartButton не найден");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.finalize();
    }

    /**
     * добавляет услугу в корзину
     */
    private void addServiceToCart(Service service) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(context, "Сначала войдите в аккаунт", Toast.LENGTH_SHORT).show();
            ((Activity) context).startActivity(new Intent(context, AuthActivity.class));
            return;
        }

        boolean success = dbHelper.addToCart(userId, 0, service.getId());
        if (success) {
            Toast.makeText(context, "Услуга \"" + service.getTitle() + "\" добавлена в корзину",
                    Toast.LENGTH_SHORT).show();

            if (cartUpdateListener != null) {
                cartUpdateListener.onCartItemAdded();
            }
        } else {
            int currentTotal = dbHelper.getCartItemsCount(userId);
            if (currentTotal >= 50) {
                Toast.makeText(context, "В корзине максимум 50 товаров/услуг", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Нельзя добавить больше 5 шт. одной услуги", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * устанавливает слушатель добавления в корзину
     */
    public void setCartUpdateListener(OnCartUpdateListener listener) {
        this.cartUpdateListener = listener;
    }
}