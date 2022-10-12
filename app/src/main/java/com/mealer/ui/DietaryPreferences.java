package com.mealer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mealer.app.User;

import java.util.HashMap;

public class DietaryPreferences extends AppCompatActivity {

    // initializing all buttons
    private Button vegan, halal, kosher, glutenFree, peanutFree, none;

    // firebase authentication object
    FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietary_preferences);

        vegan = findViewById(R.id.buttonVegan);
        halal = findViewById(R.id.buttonHalal);
        kosher = findViewById(R.id.buttonKosher);
        glutenFree = findViewById(R.id.buttonGlutenFree);
        peanutFree = findViewById(R.id.buttonPeanutFree);
        none = findViewById(R.id.buttonNone);

        // getting instance of firebase authentication
        this.mAuth = FirebaseAuth.getInstance();

        // setting on click listeners for each button
        vegan.setOnClickListener(v->setRestriction("vegan"));
        halal.setOnClickListener(v->setRestriction("halal"));
        kosher.setOnClickListener(v->setRestriction("kosher"));
        glutenFree.setOnClickListener(v->setRestriction("gluten free"));
        peanutFree.setOnClickListener(v->setRestriction("peanut free"));
        none.setOnClickListener(v->setRestriction("none"));
    }

    /**
     * method for adding a dietary restriction to the users account so meals they are not
     * recommended meals that they can't eat
     * @param type the type of dietary restriction the user wants to add to their account
     */
    private void setRestriction(String type){
        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                        .getReference("users/client");

        HashMap<String, Object> update = new HashMap<>();
        update.put("dietary restriction", type);

        FirebaseUser currentFirebaseUser = this.mAuth.getCurrentUser();
        if(currentFirebaseUser!=null){
            databaseReference.child(currentFirebaseUser.getUid()).updateChildren(update);

        }

        finish();
        // send user to home page and pass on the current signed in user object
        Intent intent = new Intent(this, UserHomePage.class);
        intent.putExtra("TYPE", (User) getIntent().getParcelableExtra("TYPE"));
        startActivity(intent);
    }
}