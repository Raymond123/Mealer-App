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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mealer.app.ClientUser;
import com.mealer.app.Order;
import com.mealer.app.User;
import com.mealer.app.menu.MenuItem;
import com.mealer.ui.OnFragmentInteractionListener;
import com.mealer.ui.R;
import com.mealer.ui.databinding.FragmentComplaintBinding;
import com.mealer.ui.databinding.FragmentMenuItemDetailsBinding;

import java.util.Locale;

public class OrderDetailsFragment extends Fragment {

    private final int navToSearch = R.id.action_navigation_order_item_details_to_navigation_notifications;
    private final String TAG = "OrderDetailsFragment";

    private FragmentMenuItemDetailsBinding binding;
    private OnFragmentInteractionListener mListener;
    private FirebaseUser currentUser;
    private Bundle args;
    private Order order;
    private MenuItem menuItem;
    private User user;

    private TextView title;
    private TextView caloriesTitle;
    private EditText name;
    private EditText desc;
    private EditText calories;
    private EditText ingredients;

    private Button accept;
    private Button reject;
    private CheckBox remove;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentMenuItemDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        root.setBackgroundColor(Color.parseColor("#FEFAE0"));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = this.requireActivity().getIntent();
        try{
            user = intent.getParcelableExtra("TYPE");
        }catch (ClassCastException cast){
            cast.printStackTrace();
        }

        args = getArguments();
        if(args != null) {
            order = args.getParcelable("ORDER");
            menuItem = order.getOrderItem();
        }

        title = binding.menuItemInfo;
        caloriesTitle = binding.itemCaloriesLabel;


        title.setText("Order From: \n");
        DatabaseReference dbRef = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("users");

        accept = binding.addNewItem;
        accept.setText("Accept Order");
        reject = binding.delete;
        reject.setText("Reject Order");

        remove = binding.isActiveBox;
        remove.setVisibility(View.GONE);

        name = binding.itemNameText;
        name.setText(menuItem.getItemName());
        desc = binding.itemDescriptionText;
        desc.setText(menuItem.getItemDescription());
        calories = binding.itemCaloriesText;
        // set calories as dietary restriction
        ingredients = binding.itemIngredients;
        ingredients.setText(menuItem.getMainIngredients());


        if(user.getUserType().equals("cook")){
            accept.setOnClickListener(onClick -> setOrderStatus(true));
            reject.setOnClickListener(onClick -> setOrderStatus(false));

            if(order.getOrderStatus().equals("accepted")){
                reject.setVisibility(View.GONE);
                accept.setText("Order Completed");
                accept.setOnClickListener(onCLick -> removeItem(true)); //do nothing atm
            }else if(order.getOrderStatus().equals("rejected")){
                reject.setVisibility(View.GONE);
                accept.setText("Delete Order");
                accept.setOnClickListener(onCLick -> removeItem(false));
            }
        }else{
            title.setText("Order " + order.getOrderStatus());
            accept.setText("Return to My Orders");
            accept.setOnClickListener(onClick -> updateUI(null, navToSearch));
            calories.setText(menuItem.getCalories());
            reject.setVisibility(View.GONE);
        }

        dbRef.child(order.getOrderFrom()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DataSnapshot userData = task.getResult();
                ClientUser user = userData.getValue(ClientUser.class);
                if (user != null) {
                    String fName = user.getFirstName();
                    String lName = user.getLastName();

                    if(user.getUserType().equals("cook")) {
                        title.setText("Order From: \n" +
                                fName.substring(0, 1).toUpperCase(Locale.ROOT) + fName.substring(1) + " " +
                                lName.substring(0, 1).toUpperCase(Locale.ROOT) + lName.substring(1));

                        caloriesTitle.setText("Dietary Restrictions");
                        calories.setText(user.getDietaryRestriction());
                    }
                }
            }else{
                Log.e(TAG, "Error");
            }
        });

        return root;
    }

    private void setOrderStatus(boolean status){
        if(status){
            order.accepted(currentUser.getUid());
        }else{
            order.rejected(currentUser.getUid());
        }

        updateUI(null, navToSearch);
    }

    private void removeItem(boolean status){
        if(status){
            order.completed(currentUser.getUid());
        }else{
            order.deleted(currentUser.getUid());
        }

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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
