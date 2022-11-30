package com.mealer.ui.ui.account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mealer.app.Admin;
import com.mealer.app.ClientUser;
import com.mealer.app.CookUser;
import com.mealer.app.User;
import com.mealer.ui.LoginPage;
import com.mealer.ui.OnFragmentInteractionListener;
import com.mealer.ui.R;
import com.mealer.ui.databinding.FragmentAccountPageBinding;

import java.util.HashMap;
import java.util.Objects;

public class AccountPageFragment extends Fragment {

    private static final String TAG = "AccountPageFragment";

    private static final int reload = R.id.action_navigation_account_self;
    private OnFragmentInteractionListener mListener;

    // initializing activity elements
    private TextView userType;
    private TextView userName;
    private TextView userDescription;
    private Button signOut;

    private Admin signedIn;

    FragmentAccountPageBinding binding;
    DatabaseReference dbRef;
    FirebaseAuth mAuth;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.getRoot().setBackgroundColor(Color.parseColor("#FEFAE0"));

        mAuth = FirebaseAuth.getInstance();

        Intent intent = this.requireActivity().getIntent();
        userType = binding.userType;
        userName = binding.userName;
        userDescription = binding.userCookDescription;

        // get current user object from intent
        try {
            this.signedIn = intent.getParcelableExtra("TYPE");
        } catch (ClassCastException cast){
            cast.printStackTrace();
        }

        // get current firebase user from firebase authentication
        FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference("users");
        // display user type on home page

        signOut = binding.signOut;
        signOut.setOnClickListener(c->signOut(currentFirebaseUser));

        if(this.signedIn.getClass() != Admin.class && currentFirebaseUser != null){
            if(getArguments() != null) {
                Bundle args = requireArguments();
                CookUser user = args.getParcelable("COOK");
                String cookId = args.getString("ID");
                userName.setText(user.getFirstName() + " " + user.getLastName());
                userType.setText("Cook Rating: " + user.getRating());
                userDescription.setText("Cook Description: \n" + user.getDescription());
                Log.d(TAG, "userType: " + this.signedIn.getUserType());

                signOut.setText("Rate Cook");
                if(signOut.hasOnClickListeners()){
                    signOut.setOnClickListener(onClick -> {
                        // popup rating input
                        HashMap<String, Object> update = new HashMap<>();
                        int rating = (20 + user.getIntRating())/2; // 50 == rating input
                        user.setRating(rating);
                        update.put("rating", String.valueOf(user.getIntRating()));
                        dbRef.child(cookId).updateChildren(update);

                        updateUI(getArguments(), reload);
                    });
                }
            }else {
                dbRef.child(currentFirebaseUser.getUid()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot user = task.getResult();
                        try {
                            CookUser currentUser = user.getValue(CookUser.class);
                            if(currentUser!=null) {
                                if(currentUser.getUserType().equals("client")){
                                    throw new ClassCastException();
                                }
                                userName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
                                userType.setText("Cook Rating: " + currentUser.getRating());
                                userDescription.setText("Cook Description: \n" + currentUser.getDescription());
                                Log.d(TAG, "userType: " + this.signedIn.getUserType());
                            }else{
                                Log.d(TAG, "currentUser is null");
                            }
                        } catch (ClassCastException client) {
                            ClientUser currentUser = user.getValue(ClientUser.class);
                            if(currentUser != null) {
                                userName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
                                userType.setText("welcome client");

                                // Centers the welcome text
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                params.gravity = Gravity.CENTER;
                                userType.setLayoutParams(params);

                                Log.d(TAG, "userType: " + this.signedIn.getUserType());
                            }else{
                                Log.d(TAG, "currentUser is null");
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting user");
                    }
                });
            }
        }else{
            userName.setText("Welcome Admin!");
            userType.setText("User Type: " + this.signedIn.getUserType());
            Log.d(TAG, "userType: " + this.signedIn.getUserType());
        }

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
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


    /**
     * signs current user that is signed in to firebase authentication out and sends to login page
     * @param currentFirebaseUser current user that is signed in
     */
    private void signOut(FirebaseUser currentFirebaseUser){
        if(currentFirebaseUser!=null){
            mAuth.signOut();
        }
        setArguments(null);
        startActivity(new Intent(this.getContext(), LoginPage.class));
    }
}