package com.mealer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mealer.app.*;

//SIGN UP
public class SignUpPageClient extends AppCompatActivity {

    private final String TAG = "SignUpPageClient";

    EditText fName, lName, email, password, confirmPassword, address, cardNumber, cardSecurity, cardExpiry;
    Button createAccount;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page_client);

        fName = findViewById(R.id.createAccountFirstName);
        lName = findViewById(R.id.createAccountLastName);
        email = findViewById(R.id.createAccountEmail);
        password = findViewById(R.id.createAccountPassword);
        confirmPassword = findViewById(R.id.createAccountPassword2);
        address = findViewById(R.id.createAccountAddress);
        // TODO: store securely
        cardNumber = findViewById(R.id.createAccountCardNumber);
        cardExpiry = findViewById(R.id.createAccountCardExpiry);
        cardSecurity = findViewById(R.id.createAccountCardSecurity);

        mAuth = FirebaseAuth.getInstance();

        createAccount = findViewById(R.id.signupButton);
        createAccount.setOnClickListener(v -> {
            String passwordText = password.getText().toString();
            if(passwordText.equals(confirmPassword.getText().toString())) {
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), passwordText)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Log.d(TAG, "createUserWithEmailSuccess");
                                FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();
                                assert currentFirebaseUser != null;
                                User currentUser = new ClientUser(fName.getText().toString(), lName.getText().toString(),
                                        email.getText().toString(), address.getText().toString(), cardNumber.getText().toString(),
                                        cardExpiry.getText().toString(), cardSecurity.getText().toString(), currentFirebaseUser.getUid());
                                updateUI(currentFirebaseUser, currentUser);
                            }else{
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                //PopupWindow failWindow = new PopupWindow(View, width, height, true);
                                //failWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                                Toast.makeText(SignUpPageClient.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null, null);
                            }
                        });
            }
        });
    }

    // copied from firebase documentation
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser, null); //TODO: currentUser cant be null change
        }
    }

    private void updateUI(FirebaseUser currentFirebaseUser, User currentUser){
        if (currentFirebaseUser == null){
            finish();
            startActivity(getIntent());
        }

        Intent signIn = new Intent(SignUpPageClient.this, DietaryPreferences.class);
        //signIn.putExtra("USER", currentUser);
        startActivity(signIn);
    }
}