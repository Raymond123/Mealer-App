package com.mealer.ui.ui.search;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mealer.app.CookUser;
import com.mealer.app.Order;
import com.mealer.app.menu.MenuItem;
import com.mealer.ui.OnFragmentInteractionListener;
import com.mealer.ui.R;
import com.mealer.ui.databinding.FragmentMenuItemDetailsBinding;

import java.util.Objects;
import java.util.UUID;

public class SearchDetailsFragment extends Fragment {

    private final String TAG = "SearchDetailsFragment";
    private final int navToSearch = R.id.action_navigation_menu_item_details_to_navigation_client_home;
    private final int navToAcc = R.id.action_navigation_menu_item_details_to_navigation_account;


    private FragmentMenuItemDetailsBinding binding;
    private OnFragmentInteractionListener mListener;
    NotificationManagerCompat manager;
    private DatabaseReference cookRef;
    private DatabaseReference orderRef;
    private Bundle args;
    private MenuItem item;
    private String cookId;

    private FirebaseAuth mAuth;

    private EditText name;
    private EditText desc;
    private EditText calories;
    private EditText ingredients;
    private EditText price;

    private Button placeOrder;
    private CheckBox remove;
    private Button removeButton;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentMenuItemDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        root.setBackgroundColor(Color.parseColor("#FEFAE0"));

        manager = NotificationManagerCompat.from(this.requireContext());
        mAuth = FirebaseAuth.getInstance();
        cookRef = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("users");

        orderRef = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("orders");

        name = binding.itemNameText;
        desc = binding.itemDescriptionText;
        calories = binding.itemCaloriesText;
        ingredients = binding.itemIngredients;
        price = binding.itemPriceText;

        args = getArguments();
        if(args!=null) {
            item = args.getParcelable("ITEM");
            cookId = args.getString("COOK");
        }

        if(item!=null){
            name.setText(item.getItemName());
            desc.setText(item.getItemDescription());
            calories.setText(item.getCalories());
            ingredients.setText(item.getMainIngredients());
            price.setText(String.valueOf(item.getPrice()));
        }

        name.setEnabled(false);
        name.setBackgroundResource(android.R.color.transparent);

        desc.setEnabled(false);
        desc.setBackgroundResource(android.R.color.transparent);

        calories.setEnabled(false);
        calories.setBackgroundResource(android.R.color.transparent);

        ingredients.setEnabled(false);
        ingredients.setBackgroundResource(android.R.color.transparent);

        price.setEnabled(false);
        price.setBackgroundResource(android.R.color.transparent);

        placeOrder = binding.delete;
        placeOrder.setText("Place Order");

        remove = binding.isActiveBox;
        remove.setVisibility(View.GONE);

        removeButton = binding.addNewItem;
        removeButton.setText("View Cook Account Page");
        //removeButton.setVisibility(View.GONE);

        placeOrder.setOnClickListener(onCLick -> placeOrder(item));
        removeButton.setOnClickListener(onClick -> {
            args = new Bundle();
            // args.putParcelable(); put cook user here / cook id
            // go to cook account page
            updateUI(args, navToAcc);
        });

        return root;
    }

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    notify(manager);
                    Log.d(TAG, "notifs allowed");
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Toast.makeText(this.requireContext(), "Notifications Disabled", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "notifs not allowed");
                }
            });

    //@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void placeOrder(MenuItem item){
        if(shouldShowRequestPermissionRationale(Notification.CATEGORY_EVENT)){
            Log.d(TAG, "requesting");
        }

        NotificationManagerCompat manager = NotificationManagerCompat.from(this.requireContext());
        if(manager.areNotificationsEnabled()){
            notify(manager);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
        }

        if(mAuth.getCurrentUser() != null) {
            Order order = new Order(item, "pending", mAuth.getCurrentUser().getUid());
            cookRef.child(cookId).child("orders").child(order.getOrderId()).setValue(order);
        }

        updateUI(null, navToSearch);
    }

    private void notify(NotificationManagerCompat notificationManagerCompat){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.requireContext(), "lemubitA")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Order Placed")
                .setContentText("Your order has been placed. Please wait for the cook to action your order.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManagerCompat.notify(item.getItemId().hashCode(), builder.build());
    }

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
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
