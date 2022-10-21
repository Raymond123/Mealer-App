package com.mealer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mealer.app.User;

// GET STARTED
public class MainActivity extends AppCompatActivity {

    Button signIn, accountExists;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started_page);

        // firebase authentication instance for checking if user is already signed in
        mAuth = FirebaseAuth.getInstance();

        signIn = findViewById(R.id.getStartedButton);
        // sends to picking user type if user wishes to create new account
        signIn.setOnClickListener(
                v -> startActivity(
                        new Intent(MainActivity.this, PickingUserType.class)
                ));

        accountExists = findViewById(R.id.accountExists);
        // sends to login page if user already has an account but needs to login
        accountExists.setOnClickListener(x->startActivity(
                new Intent(MainActivity.this, LoginPage.class)
        ));
    }

    // copied from firebase documentation
    @Override
    public void onStart() {
        super.onStart();
        //mAuth.signOut();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            /* if user is logged into firebase authentication already
            sends user to login page where login page will detect the same thing,
            and create a new user object with the users attributes */
            startActivity(new Intent(this, LoginPage.class));
        }
    }

}