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

    private Button vegan, halal, kosher, glutenFree, peanutFree, none;

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

        this.mAuth = FirebaseAuth.getInstance();

        vegan.setOnClickListener(v->setRestriction("vegan"));
        halal.setOnClickListener(v->setRestriction("halal"));
        kosher.setOnClickListener(v->setRestriction("kosher"));
        glutenFree.setOnClickListener(v->setRestriction("gluten free"));
        peanutFree.setOnClickListener(v->setRestriction("peanut free"));
        none.setOnClickListener(v->setRestriction("none"));
    }

    private void setRestriction(String type){
        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                        .getReference("users/client");

        HashMap<String, Object> update = new HashMap<>();
        update.put("dietary restriction", type);

        FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();
        assert currentFirebaseUser != null;
        databaseReference.child(currentFirebaseUser.getUid()).updateChildren(update);

        finish();
        startActivity(new Intent(this, LoginPage.class));
    }
}