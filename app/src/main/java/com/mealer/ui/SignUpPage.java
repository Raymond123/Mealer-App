package com.mealer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.mealer.app.*;

//SIGN UP
public class SignUpPage extends AppCompatActivity {

    EditText email;
    EditText password;
    EditText confirmPassword;

    Button createAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        email = findViewById(R.id.createAccountEmail);
        password = findViewById(R.id.createAccountPassword);
        confirmPassword = findViewById(R.id.createAccountPassword2);

        createAccount = findViewById(R.id.signupButton);
        createAccount.setOnClickListener(v -> {
            String passwordText = password.getText().toString();
            if(passwordText.equals(confirmPassword.getText().toString())) {
                User signedIn = new CookUser("first", "last", email.getText().toString(), "123 456 7890", "address",
                        passwordText, "description");
            }
        });
    }
}