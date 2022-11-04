package com.mealer.ui.ui.complaint;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mealer.ui.R;

public class ComplaintsViewHolder extends RecyclerView.ViewHolder {

    TextView subjectText;
    TextView cookIdText;

    public ComplaintsViewHolder(@NonNull View itemView) {
        super(itemView);
        subjectText = itemView.findViewById(R.id.listSubject);
        cookIdText = itemView.findViewById(R.id.listCookId);
    }
}
