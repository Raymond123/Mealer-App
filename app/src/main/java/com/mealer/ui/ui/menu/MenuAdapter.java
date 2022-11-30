package com.mealer.ui.ui.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mealer.app.menu.MenuItem;
import com.mealer.ui.R;

import java.util.List;


public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder> {

    Context context;
    List<MenuItem> menuItemList;

    public MenuAdapter(Context context, List<MenuItem> menuItemList){
        this.context = context;
        this.menuItemList = menuItemList;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MenuViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.menu_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        holder.name.setText(menuItemList.get(position).getItemName());
        holder.calories.setText(menuItemList.get(position).getCalories());
        holder.description.setText(menuItemList.get(position).getItemDescription());
        holder.price.setText(String.valueOf(menuItemList.get(position).getPrice()));

    }

    @Override
    public int getItemCount() {
        return menuItemList.size();
    }
}
