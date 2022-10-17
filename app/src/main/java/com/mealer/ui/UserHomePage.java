package com.mealer.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mealer.app.User;
import com.mealer.ui.databinding.ActivityUserHomePageBinding;

public class UserHomePage extends AppCompatActivity {

    // initializing activity elements
    private TextView userType;
    private Button signOut;

    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        userType = findViewById(R.id.userType);

        // get current user object from intent
        User signedIn = intent.getParcelableExtra("TYPE");
        // get current firebase user from firebase authentication
        FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();
        // display user type on home page
        userType.setText(signedIn.getUserType());
        Log.d("firebase", "userType: " + signedIn.getUserType());

        signOut = findViewById(R.id.signOut);
        signOut.setOnClickListener(c->signOut(currentFirebaseUser,signedIn));
    }

    /**
     * signs current user that is signed in to firebase authentication out and sends to login page
     * @param user current user that is signed in
     */
    private void signOut(FirebaseUser currentFirebaseUser, User user){
        if(currentFirebaseUser!=null){
            mAuth.signOut();
        }
        finish();
        startActivity(new Intent(this, LoginPage.class));
    }

}