package com.mealer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    FirebaseAuth mAuth;

    EditText username;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.forgottenEmail);
        submit = findViewById(R.id.submitButton);

        submit.setOnClickListener(x->sendPasswordReset());

    }

    /**
     * uses firebase authentication built in method to send user password reset email
     */
    private void sendPasswordReset(){
        if(!username.getText().toString().isEmpty()){
            mAuth.sendPasswordResetEmail(username.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                    this,
                                    "Email sent, If you didn't receive it check your junk mail",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(
                                    this,
                                    "User does not exist",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
            finish();
        }else{
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
        }
    }
}