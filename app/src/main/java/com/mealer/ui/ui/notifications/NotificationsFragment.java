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
import com.mealer.app.CookUser;
import com.mealer.app.Order;
import com.mealer.app.User;
import com.mealer.app.menu.MenuItem;
import com.mealer.ui.OnFragmentInteractionListener;
import com.mealer.ui.R;
import com.mealer.ui.databinding.FragmentComplaintsListBinding;
import com.mealer.ui.databinding.FragmentNotificationsBinding;
import com.mealer.ui.ui.RecyclerItemClickListener;
import com.mealer.ui.ui.search.SearchAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class NotificationsFragment extends Fragment {

    private final int navToDetails = R.id.action_navigation_notifications_to_navigation_order_item_details;
    private final String TAG = "NotificationsFragment";

    private FragmentComplaintsListBinding binding;
    private OnFragmentInteractionListener mListener;
    private DatabaseReference dbRef;
    private Bundle args;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private User user;
    private ArrayList<Order> orderList;
    private Context context;

    private TextView title;
    private RecyclerView orders;

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentComplaintsListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.getRoot().setBackgroundColor(Color.parseColor("#FEFAE0"));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        context = this.getContext();

        args = getArguments();

        title = binding.complaintsTextView;
        orders = binding.complaintsView;

        orderList = new ArrayList<>();
        OrderAdapter orderAdapter = new OrderAdapter(context, orderList);
        orders.setLayoutManager(new LinearLayoutManager(context));
        orders.setAdapter(orderAdapter);
        orders.addOnItemTouchListener(menuItemDetails);

        Intent intent = this.requireActivity().getIntent();
        try{
            user = intent.getParcelableExtra("TYPE");
        }catch (ClassCastException cast){
            cast.printStackTrace();
        }

        dbRef = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("users");

        if(user.getUserType().equals("cook")){
            title.setText("Order Requests");

            dbRef.child(currentUser.getUid()).child("orders").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    orderList.clear();
                    ArrayList<Order> pending = new ArrayList<>();
                    ArrayList<Order> rejected = new ArrayList<>();

                    DataSnapshot data = task.getResult();
                    for(DataSnapshot child : data.getChildren()) {
                        Order order = child.getValue(Order.class);
                        if (order != null && !order.getOrderStatus().equals("deleted") && !order.getOrderStatus().equals("completed")) {
                            if(order.getOrderStatus().equals("rejected")){
                                rejected.add(order);
                            }else if (order.getOrderStatus().equals("pending")){
                                pending.add(order);
                            }else{
                                orderList.add(order);
                            }
                            order.setOrderId(child.getKey());
                            Log.d(TAG, "order added");
                        }
                    }

                    orderList.addAll(pending);
                    orderList.addAll(rejected);
                    orderAdapter.notifyDataSetChanged();
                }else{
                    Log.e(TAG, "error");
                }
            });
        }else{
            title.setText("My Orders");

            dbRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DataSnapshot data = task.getResult();
                    for(DataSnapshot child : data.getChildren()){
                        try {
                            if ("cook".equals(Objects.requireNonNull(child.getValue(CookUser.class)).getUserType())) {
                                for (DataSnapshot grandChild : child.child("orders").getChildren()) {
                                    Order order = grandChild.getValue(Order.class);
                                    if (order != null && currentUser.getUid().equals(order.getOrderFrom())) {
                                        orderList.add(order);
                                    }
                                }
                                orderAdapter.notifyDataSetChanged();
                            }
                        }catch (NullPointerException ignore){}
                    }
                }else{
                    Log.e(TAG, "client error");

                }
            });
        }


        return root;
    }

    /**
     * the onTouch listener for the activeMenu item
     * listens for when an item is touched, and opens the details fragment
     */
    private final RecyclerView.OnItemTouchListener menuItemDetails =
            new RecyclerItemClickListener(context, this.orders,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Bundle args = new Bundle();
                            Order item = orderList.get(position);
                            args.putParcelable("ORDER", item);
                            updateUI(args, navToDetails);
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {

                        }
                    });

    /**
     * gets the mListener object from the fragments context in order to be able to return to
     * previous fragment and get the complaint info passed to this fragment
     * @param context fragment context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void updateUI(Bundle args, int id) {
        mListener.changeFragment(args, id);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}