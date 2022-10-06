package com.mealer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

// GET STARTED
public class MainActivity extends AppCompatActivity {

    Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started_page);

        signIn = findViewById(R.id.getStartedButton);
        signIn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUpPageClient.class));
        });
    }
}