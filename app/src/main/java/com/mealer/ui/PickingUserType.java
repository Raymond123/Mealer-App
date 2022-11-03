package com.mealer.ui;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.os.Bundle;
import android.widget.ImageButton;

public class PickingUserType extends AppCompatActivity {

    // initializing activity buttons
    public ImageButton btnChoice1;
    public ImageButton btnChoice2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking_user_type);
        // change the top bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.Cream));
        }
        // hide title bar
        getSupportActionBar().hide();
        // prompts to User account sign up
        btnChoice1 = findViewById(R.id.clientImageButton);
        btnChoice1.setOnClickListener(v -> openSignUpPageClient());

        // prompts to Cook account sign up
        btnChoice2 = findViewById(R.id.cookImageButton);
        btnChoice2.setOnClickListener(v -> openSignUpPageCook());
    }

    // sends user to the signup page for creating a client account
    public void openSignUpPageClient(){
        Intent intent = new Intent(this, SignUpPageClient.class);
        startActivity(intent);
    }

    // sends user to the signup page for creating a cook account
    public void openSignUpPageCook(){
        Intent intent = new Intent(this, SignUpPageCook.class);
        startActivity(intent);
    }
}