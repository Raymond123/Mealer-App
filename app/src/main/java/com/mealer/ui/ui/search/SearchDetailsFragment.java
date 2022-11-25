package com.mealer.ui.ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mealer.app.CookUser;
import com.mealer.app.Order;
import com.mealer.app.menu.MenuItem;
import com.mealer.ui.OnFragmentInteractionListener;
import com.mealer.ui.R;
import com.mealer.ui.databinding.FragmentMenuItemDetailsBinding;

import java.util.UUID;

public class SearchDetailsFragment extends Fragment {

    private final String TAG = "SearchDetailsFragment";
    private final int navToSearch = R.id.action_navigation_menu_item_details_to_navigation_client_home;

    private FragmentMenuItemDetailsBinding binding;
    private OnFragmentInteractionListener mListener;
    private DatabaseReference cookRef;
    private DatabaseReference orderRef;
    private Bundle args;
    private MenuItem item;
    private String cookId;

    private EditText name;
    private EditText desc;
    private EditText calories;
    private EditText ingredients;

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
        }

        name.setEnabled(false);
        name.setBackgroundResource(android.R.color.transparent);

        desc.setEnabled(false);
        desc.setBackgroundResource(android.R.color.transparent);

        calories.setEnabled(false);
        calories.setBackgroundResource(android.R.color.transparent);

        ingredients.setEnabled(false);
        ingredients.setBackgroundResource(android.R.color.transparent);

        placeOrder = binding.delete;
        placeOrder.setText("Place Order");

        remove = binding.isActiveBox;
        remove.setVisibility(View.GONE);

        removeButton = binding.addNewItem;
        removeButton.setVisibility(View.GONE);

        placeOrder.setOnClickListener(onCLick -> placeOrder(item));

        return root;
    }

    private void placeOrder(MenuItem item){
        Order order = new Order(item, "pending");
        cookRef.child(cookId).child("orders").child(order.getOrderId()).setValue(order);

        updateUI(null, navToSearch);
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
