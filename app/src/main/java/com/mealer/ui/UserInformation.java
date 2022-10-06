package com.mealer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

// TODO: integrate this with SignUp pages, user should input all their info in a single activity
public class UserInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
    }
}