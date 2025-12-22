package com.example.firstbit_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.firstbit_app.Models.OrderItem;
import com.example.firstbit_app.R;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private Context context;
    private List<OrderItem> items;

    public OrderDetailAdapter(Context context, List<OrderItem> items) {
        this.context = context;
        this.items = items;
    }

    /**
     * создаёт ViewHolder для элемента заказа
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_detail_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * привязывает данные товара к ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = items.get(position);

        holder.title.setText(item.getTitle());
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        holder.total.setText(String.format("%,d ₽", item.getTotalPrice()));

        if (item.getType().equals("service")) {
            holder.typeTag.setText("Услуга");
            holder.typeTag.setVisibility(View.VISIBLE);
            holder.deadline.setVisibility(View.VISIBLE);
            holder.deadline.setText("Срок: " + item.getDeadline());
        } else {
            holder.typeTag.setVisibility(View.GONE);
            holder.deadline.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    /**
     * вложанный класс
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, typeTag, deadline, quantity, total;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            typeTag = itemView.findViewById(R.id.item_type);
            deadline = itemView.findViewById(R.id.item_deadline);
            quantity = itemView.findViewById(R.id.item_quantity);
            total = itemView.findViewById(R.id.item_total);
        }
    }
}
