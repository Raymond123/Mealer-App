package com.mealer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mealer.app.NotificationService;
import com.mealer.app.User;

// GET STARTED
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Button signIn, accountExists;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started_page);
        createNotificationChannel();

        // hide title bar
        getSupportActionBar().hide();
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
        accountExists.setOnClickListener(x-> {
            startActivity(
                    new Intent(MainActivity.this, LoginPage.class)
            );
            finish();
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("lemubitA", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
            Log.d(TAG, "Closing");
            startActivity(new Intent(this, LoginPage.class));
            finish();
        }
    }

}