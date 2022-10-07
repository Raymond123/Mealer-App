package com.mealer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

// GET STARTED
public class MainActivity extends AppCompatActivity {

    Button signIn, accountExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started_page);

        signIn = findViewById(R.id.getStartedButton);
        signIn.setOnClickListener(
                v -> startActivity(
                        new Intent(MainActivity.this, PickingUserType.class)
                ));

        accountExists = findViewById(R.id.accountExists);
        accountExists.setOnClickListener(x->startActivity(
                new Intent(MainActivity.this, LoginPage.class)
        ));
    }
}