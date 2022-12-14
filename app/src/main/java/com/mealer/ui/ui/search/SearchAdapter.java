package com.mealer.ui.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mealer.app.menu.MenuItem;
import com.mealer.ui.R;

import java.util.List;


public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {

    Context context;
    List<MenuItem> menuItemList;

    public SearchAdapter(Context context, List<MenuItem> menuItemList){
        this.context = context;
        this.menuItemList = menuItemList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.menu_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.name.setText(menuItemList.get(position).getItemName());
        holder.calories.setText(menuItemList.get(position).getCalories());
        holder.description.setText(menuItemList.get(position).getItemDescription());
    }

    @Override
    public int getItemCount() {
        return menuItemList.size();
    }
}
