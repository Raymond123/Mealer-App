package com.mealer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// GET STARTED
public class MainActivity extends AppCompatActivity {

    Button signUp;
    TextView accountExists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started_page);


        accountExists = findViewById(R.id.accountExists);
        accountExists.setOnClickListener(view -> {
                startActivity(new Intent(MainActivity.this, LoginPage.class));
        });
        signUp = findViewById(R.id.getStartedButton);
        signUp.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUpPage.class));
        });
    }
}