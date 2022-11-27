package com.mealer.ui.ui.notifications;

import android.text.Layout;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mealer.ui.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    protected TextView name;
    protected TextView calories;
    protected TextView description;
    protected TextView status;
    protected View layout;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.itemName);
        calories = itemView.findViewById(R.id.itemCalories);
        description = itemView.findViewById(R.id.itemDescription);
        status = itemView.findViewById(R.id.itemStatus);
        layout = itemView.findViewById(R.id.orderLayout);
    }
}
