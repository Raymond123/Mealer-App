package com.mealer.ui.ui.complaint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mealer.app.Complaint;
import com.mealer.ui.R;

import java.util.List;

public class ComplaintsList extends RecyclerView.Adapter<ComplaintsViewHolder> {

    Context context;
    List<Complaint> complaintList;

    public ComplaintsList(Context context, List<Complaint> complaintList){
        this.context = context;
        this.complaintList = complaintList;
    }

    @NonNull
    @Override
    public ComplaintsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ComplaintsViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.complaint_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintsViewHolder holder, int position) {
        holder.subjectText.setText(complaintList.get(position).getSubject());
        holder.cookIdText.setText(complaintList.get(position).getCookID());
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }
}
