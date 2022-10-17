package com.mealer.ui;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.ImageButton;

public class PickingUserType extends AppCompatActivity {
    public ImageButton btnChoice1;
    public ImageButton btnChoice2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking_user_type);

        // prompts to User account sign up
        btnChoice1 = findViewById(R.id.clientImageButton);
        btnChoice1.setOnClickListener(v -> openSignUpPageClient());

        // prompts to Cook account sign up
        btnChoice2 = findViewById(R.id.cookImageButton);
        btnChoice2.setOnClickListener(v -> openSignUpPageCook());
    }

    public void openSignUpPageClient(){
        Intent intent = new Intent(this, SignUpPageClient.class);
        startActivity(intent);
    }
    public void openSignUpPageCook(){
        Intent intent = new Intent(this, SignUpPageCook.class);
        startActivity(intent);
    }
}