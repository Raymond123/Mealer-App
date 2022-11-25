package com.mealer.ui.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mealer.app.Order;
import com.mealer.app.User;
import com.mealer.app.menu.MenuItem;
import com.mealer.ui.databinding.FragmentNotificationsBinding;
import com.mealer.ui.ui.search.SearchAdapter;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private final String TAG = "NotificationsFragment";

    private FragmentNotificationsBinding binding;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private User user;
    private ArrayList<Order> orderList;
    private Context context;

    private TextView title;
    private RecyclerView orders;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.getRoot().setBackgroundColor(Color.parseColor("#FEFAE0"));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        context = this.getContext();

        title = binding.textNotifications;
        orders = binding.ordersView;

        Intent intent = this.requireActivity().getIntent();
        try{
            user = intent.getParcelableExtra("TYPE");
        }catch (ClassCastException cast){
            cast.printStackTrace();
        }

        if(user.getUserType().equals("cook")){
            title.setText("Order Requests");
        }else{
            title.setText("My Orders");
        }


        dbRef = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("users");

        dbRef.child(currentUser.getUid()).child("orders").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                orderList = new ArrayList<>();
                DataSnapshot data = task.getResult();
                for(DataSnapshot child : data.getChildren()) {
                    Order order = child.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                        Log.d(TAG, "order added");
                    }
                }

                orders.setLayoutManager(new LinearLayoutManager(context));
                orders.setAdapter(new OrderAdapter(context, orderList));
            }else{
                Log.e(TAG, "error");
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}