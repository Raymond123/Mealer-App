package com.mealer.ui;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;

public class AccountType extends AppCompatActivity {
    public Button btnChoice1;
    public Button btnChoice2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_type);

        btnChoice1 = (Button)findViewById(R.id.btnChoice1);             // prompts to User account sign up
        btnChoice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpPageClient();

            }
        });

        btnChoice2 = (Button)findViewById(R.id.btnChoice2);            // prompts to Cook account sign up
        btnChoice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpPageCook();

            }
        });
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