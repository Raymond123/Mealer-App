package com.mealer.ui.ui.search;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mealer.ui.R;

public class SearchViewHolder extends RecyclerView.ViewHolder {

    protected TextView name;
    protected TextView calories;
    protected TextView description;

    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.itemName);
        calories = itemView.findViewById(R.id.itemCalories);
        description = itemView.findViewById(R.id.itemDescription);
    }
}
