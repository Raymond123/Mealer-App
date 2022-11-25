package com.mealer.ui.ui.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mealer.app.Order;
import com.mealer.app.menu.MenuItem;
import com.mealer.ui.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList){
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.order_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        MenuItem item = orderList.get(position).getOrderItem();
        holder.name.setText(item.getItemName());
        holder.description.setText(item.getItemDescription());
        holder.calories.setText(item.getCalories());
        holder.status.setText(orderList.get(position).getOrderStatus());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
