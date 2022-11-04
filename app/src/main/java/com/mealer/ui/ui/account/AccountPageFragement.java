package com.mealer.ui.ui.account;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mealer.app.Admin;
import com.mealer.ui.ClientHomePage;
import com.mealer.ui.LoginPage;
import com.mealer.ui.R;
import com.mealer.ui.databinding.FragmentAccountPageBinding;
import com.mealer.ui.databinding.FragmentHomeBinding;
import com.mealer.ui.ui.home.HomeViewModel;

public class AccountPageFragement extends Fragment {

    // initializing activity elements
    private TextView userType;
    private Button signOut;

    private Admin signedIn;

    FragmentAccountPageBinding binding;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AccountPageViewModel viewModel =
                new ViewModelProvider(this).get(AccountPageViewModel.class);

        binding = FragmentAccountPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.getRoot().setBackgroundColor(Color.parseColor("#FEFAE0"));

        mAuth = FirebaseAuth.getInstance();

        Intent intent = this.getActivity().getIntent();
        userType = binding.userType;

        // get current user object from intent
        try {
            this.signedIn = intent.getParcelableExtra("TYPE");
        } catch (ClassCastException cast){
            cast.printStackTrace();
        }

        // get current firebase user from firebase authentication
        FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();
        // display user type on home page
        userType.setText("User Type: " + this.signedIn.getUserType());
        Log.d("firebase", "userType: " + this.signedIn.getUserType());

        signOut = binding.signOut;
        signOut.setOnClickListener(c->signOut(currentFirebaseUser,this.signedIn));

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    /**
     * signs current user that is signed in to firebase authentication out and sends to login page
     * @param user current user that is signed in
     */
    private void signOut(FirebaseUser currentFirebaseUser, Admin user){
        if(currentFirebaseUser!=null){
            mAuth.signOut();
        }
        startActivity(new Intent(this.getContext(), LoginPage.class));
    }
}