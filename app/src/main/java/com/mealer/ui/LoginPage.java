package com.mealer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mealer.app.User;

// LOGIN PAGE
public class LoginPage extends AppCompatActivity {

    private final String TAG = "LoginPage";
    private String userType;

    EditText username, password;
    Button loginButton, forgotPassword;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.loginPageUsername);
        password = findViewById(R.id.loginPagePassword);

        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPasswordLogin);

        loginButton.setOnClickListener(v->{
            mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(x->{
                        if(x.isSuccessful()){
                            Log.d(TAG, "loginUserWithEmailSuccess");
                            FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();
                            getUser(currentFirebaseUser);
                        }else{
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "loginUserWithEmail:failure", x.getException());
                            Toast.makeText(this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null, null);
                        }
                    });
        });

    }

    private void getUser(FirebaseUser currentFirebaseUser){
        mDatabase = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("users");

        mDatabase.child(currentFirebaseUser.getUid()).child("userType").get()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        Log.e("firebase", "cannot find user", task.getException());
                    }else{
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        updateUI(currentFirebaseUser, String.valueOf(task.getResult().getValue()));
                    }
                }
        );
    }

    private void updateUI(FirebaseUser currentFirebaseUser, String currentUser){
        if (currentFirebaseUser == null){
            finish();
            startActivity(getIntent());
            return;
        }

        Intent signIn = new Intent(this, UserHomePage.class);
        signIn.putExtra("TYPE", currentUser);
        startActivity(signIn);
    }

}