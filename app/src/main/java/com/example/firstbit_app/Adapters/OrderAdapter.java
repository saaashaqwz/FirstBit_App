package com.example.firstbit_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.firstbit_app.Models.Order;
import com.example.firstbit_app.R;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.orderId.setText("Заказ №" + order.getId());
        holder.orderTotal.setText(String.format("%,d ₽", order.getTotal()));
        holder.orderStatus.setText(order.getStatus());

        if (order.getDeadline() != null && !order.getDeadline().equals("N/A")) {
            holder.orderDeadline.setText("Срок: " + order.getDeadline());
            holder.orderDeadline.setVisibility(View.VISIBLE);
        } else {
            holder.orderDeadline.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderTotal, orderStatus, orderDeadline;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            orderTotal = itemView.findViewById(R.id.order_total);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderDeadline = itemView.findViewById(R.id.order_deadline);
        }
    }
}