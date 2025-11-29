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

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

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
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
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
