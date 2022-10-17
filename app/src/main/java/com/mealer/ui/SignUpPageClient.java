package com.mealer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
            if(passwordText.equals(confirmPassword.getText().toString()) && validateAddress() && validateName()) {
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), passwordText)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Log.d(TAG, "createUserWithEmailSuccess");
                                FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();
                                assert currentFirebaseUser != null;
                                User currentUser = new ClientUser(fName.getText().toString(), lName.getText().toString(),
                                        email.getText().toString(), address.getText().toString(), cardNumber.getText().toString(),
                                        cardExpiry.getText().toString(), cardSecurity.getText().toString(), currentFirebaseUser.getUid(), "client");
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


    // check if first and last name are empty
    private boolean validateName(){
        fName = (EditText)findViewById(R.id.createAccountFirstName);
        lName = (EditText)findViewById(R.id.createAccountLastName);
        if(fName.getText().toString().matches("")){
            Toast.makeText(SignUpPageClient.this, "First name cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if(lName.getText().toString().matches("")){
            Toast.makeText(SignUpPageClient.this, "Last name cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    // check if address is empty
    private boolean validateAddress(){
        address = (EditText)findViewById(R.id.createAccountAddress);
        if(address.getText().toString().matches("")){
            Toast.makeText(SignUpPageClient.this, "Address cannot be empty!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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

    /**
     * update the android ui to start a new activity and passes the currentUser object onto the next
     * activity
     * @param currentFirebaseUser the current user logged in through firebase authentication
     * @param currentUser the current user logged in as a user object with all the users attributes
     *                    and their values
     */
    private void updateUI(FirebaseUser currentFirebaseUser, User currentUser){
        if (currentFirebaseUser == null){
            finish();
            startActivity(getIntent());
            return;
        }

        // sends the user an email in order to verify their inputted email address
        // users will not be able to login (after signing out) if email is not verified
        currentFirebaseUser.sendEmailVerification()
                .addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        //if email is successfully sent display success text
                        Toast.makeText(this,
                                "Verification email sent",
                                Toast.LENGTH_LONG).show();

                        // send user to next step in signup
                        Intent signIn = new Intent(SignUpPageClient.this, DietaryPreferences.class);
                        signIn.putExtra("TYPE", currentUser);
                        startActivity(signIn);
                    }else{
                        // if error in sending email
                        Toast.makeText(this,
                                "Error",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}