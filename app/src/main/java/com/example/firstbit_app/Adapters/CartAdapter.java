package com.example.firstbit_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstbit_app.DbHelper;
import com.example.firstbit_app.Models.Cart;
import com.example.firstbit_app.R;

import java.util.List;

/**
 * адаптер для отображения элементов корзины в RecyclerView
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Cart> carts;
    private DbHelper dbHelper;
    private OnCartUpdateListener listener;

    public interface OnCartUpdateListener {
        void onCartUpdated();
    }

    public CartAdapter(Context context, List<Cart> carts, OnCartUpdateListener listener) {
        this.context = context;
        this.carts = carts;
        this.dbHelper = new DbHelper(context, null);
        this.listener = listener;
    }

    /**
     * создаёт новый ViewHolder для элемента корзины
     */
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    /**
     * привязывает данные элемента корзины к ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = carts.get(position);

        holder.itemTitle.setText(cart.getTitle());
        holder.itemPrice.setText(String.format("%,d ₽", cart.getPrice()));
        holder.itemQuantity.setText(String.valueOf(cart.getQuantity()));
        holder.itemTotal.setText(String.format("%,d ₽", cart.getTotalPrice()));

        if (cart.getType().equals("product") && cart.getImage() != null) {
            int imageResource = context.getResources().getIdentifier(
                    cart.getImage(),
                    "drawable",
                    context.getPackageName()
            );
            if (imageResource != 0) {
                holder.itemImage.setImageResource(imageResource);
            }
        } else {
            holder.itemImage.setImageResource(R.drawable.icon_service);
        }

        if (cart.getType().equals("service")) {
            holder.itemDeadline.setVisibility(View.VISIBLE);
            holder.itemDeadline.setText("Срок: " + cart.getDeadline());
        } else {
            holder.itemDeadline.setVisibility(View.GONE);
        }

        holder.btnIncrease.setOnClickListener(v -> {
            int currentQuantity = cart.getQuantity();
            int newQuantity = currentQuantity + 1;

            cart.setQuantity(newQuantity);

            holder.itemQuantity.setText(String.valueOf(newQuantity));
            holder.itemTotal.setText(String.format("%,d ₽", cart.getTotalPrice()));

            updateQuantity(cart.getId(), newQuantity, holder);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int currentQuantity = cart.getQuantity();
            int newQuantity = currentQuantity - 1;

            if (newQuantity < 1) {
                removeItem(cart.getId(), position);
            } else {
                cart.setQuantity(newQuantity);

                holder.itemQuantity.setText(String.valueOf(newQuantity));
                holder.itemTotal.setText(String.format("%,d ₽", cart.getTotalPrice()));

                updateQuantity(cart.getId(), newQuantity, holder);
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            removeItem(cart.getId(), position);
        });
    }

    private void updateQuantity(int cartItemId, int newQuantity, CartViewHolder holder) {
        boolean success = dbHelper.updateCartItemQuantity(cartItemId, newQuantity);
        if (success) {
            if (listener != null) {
                listener.onCartUpdated();
            }
        } else {
            Toast.makeText(context, "Ошибка обновления количества", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeItem(int cartItemId, int position) {
        boolean success = dbHelper.removeFromCart(cartItemId);
        if (success) {
            carts.remove(position);
            notifyItemRemoved(position);
            if (listener != null) {
                listener.onCartUpdated();
            }
            Toast.makeText(context, "Товар удален из корзины", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Ошибка удаления товара", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    /**
     * вложанный класс
     */
    public static final class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        TextView itemPrice;
        TextView itemDeadline;
        TextView itemQuantity;
        TextView itemTotal;
        TextView btnDecrease;
        TextView btnIncrease;
        TextView btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemDeadline = itemView.findViewById(R.id.item_deadline);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemTotal = itemView.findViewById(R.id.item_total);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}