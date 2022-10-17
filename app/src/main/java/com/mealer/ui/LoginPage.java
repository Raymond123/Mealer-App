package com.mealer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.mealer.app.ClientUser;
import com.mealer.app.CookUser;
import com.mealer.app.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

// LOGIN PAGE
public class LoginPage extends AppCompatActivity {

    private final String TAG = "LoginPage";

    EditText username, password;
    Button loginButton, forgotPassword, haveAccount;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.loginPageUsername);
        password = findViewById(R.id.loginPagePassword);

        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPasswordLogin);
        haveAccount = findViewById(R.id.noAccount);

        loginButton.setOnClickListener(
                v->mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                .addOnCompleteListener(x->{
                    if(x.isSuccessful()){
                        Log.d(TAG, "loginUserWithEmailSuccess");
                        FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();
                        assert currentFirebaseUser != null;
                        getUser(currentFirebaseUser);
                    }else{
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "loginUserWithEmail:failure", x.getException());
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null, null);
                    }
                }));

        haveAccount.setOnClickListener(y->{
            finish();
            startActivity(new Intent(this, PickingUserType.class));
        });

    }

    // copied from firebase documentation
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            getUser(currentUser);
        }
    }

    private void getUser(FirebaseUser currentFirebaseUser){
        mDatabase = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("users");

        mDatabase.child(currentFirebaseUser.getUid()).get()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        Log.e("firebase", "cannot find user", task.getException());
                    }else{
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        //updateUI(currentFirebaseUser, String.valueOf(task.getResult().getValue()));
                        HashMap<String, String> userAttributes = loginUser(String.valueOf(task.getResult().getValue()));
                        try {
                            if ("cook".equals(userAttributes.get("userType"))) {
                                updateUI(currentFirebaseUser, new CookUser(userAttributes));
                            } else if ("client".equals(userAttributes.get("userType"))) {
                                updateUI(currentFirebaseUser, new ClientUser(userAttributes));
                            }else{
                                Log.e("signIn", "failed to detemine user type, userType: "+userAttributes.get("userType"));
                            }
                        }catch(NullPointerException ex){
                            Log.e("signIn", Arrays.toString(ex.getStackTrace()));
                        }
                    }
                }
        );
    }

    private HashMap<String, String> loginUser(String userAttributes){
        HashMap<String, String> userAttributesHash = new HashMap<>();
        String[] splitOne =
                userAttributes
                        .replace("{", "")
                        .replace("}", "")
                        .replace(" ", "")
                        .split(",");
        for(String index : splitOne){
            userAttributesHash.put(index.split("=")[0], index.split("=")[1]);
        }

        System.out.println(userAttributesHash.get("userType"));

        return userAttributesHash;
    }

    private void updateUI(FirebaseUser currentFirebaseUser, User currentUser){
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