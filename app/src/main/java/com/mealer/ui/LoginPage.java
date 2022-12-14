package com.mealer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mealer.app.Admin;
import com.mealer.app.ClientUser;
import com.mealer.app.CookUser;
import com.mealer.app.NotificationService;
import com.mealer.app.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

// LOGIN PAGE
public class LoginPage extends AppCompatActivity {

    private final String adminEmail = "admin@mealer.com";


    // TAG variable for marking Logs when debugging
    private final String TAG = "LoginPage";

    // initializing activity elements
    EditText username, password;
    Button loginButton, forgotPassword, haveAccount;

    // initializing firebase objects
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    String userId;
    User user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // hide title bar
        getSupportActionBar().hide();
        // get firebase authentication instance
        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.loginPageUsername);
        password = findViewById(R.id.loginPagePassword);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPasswordLogin);
        haveAccount = findViewById(R.id.noAccount);

        loginButton.setOnClickListener(
                // tries to login the user through firebase authentication with inputted email and password
                v->mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                .addOnCompleteListener(x->{
                    if(x.isSuccessful()){
                        // user is successfully signed in
                        Log.d(TAG, "loginUserWithEmailSuccess");
                        // creates instance of firebase user
                        FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();
                        assert currentFirebaseUser != null;

                        if(isAdmin(username.getText().toString())){
                            loginAdmin();
                        }else if(currentFirebaseUser.isEmailVerified()) { // checks if the user has verified their email address
                            // if email is verified, will create new user object with users attributes
                            // and log them into the home page
                            getUser(currentFirebaseUser);
                        }else{
                            // if users email is not verified will ask them to verify email address
                            Toast.makeText(this, "Please verify email address.",
                                    Toast.LENGTH_SHORT).show();
                            currentFirebaseUser.sendEmailVerification();
                        }
                    }else{
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "loginUserWithEmail:failure", x.getException());
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        // updates ui with null values
                        updateUI(null, null);
                    }
                }));

        haveAccount.setOnClickListener(y->{
            finish();
            // if the user doesn't have an account and finds themself on the login page
            // clicking will send them to the picking user page to create an account
            startActivity(new Intent(this, PickingUserType.class));
        });

        //TODO: create new activity for this action
        forgotPassword.setOnClickListener(f->{
            startActivity(new Intent(this, ForgotPassword.class));
        });

    }

    private void loginAdmin() {
        Intent signIn = new Intent(this, AdminHomePage.class);
        signIn.putExtra("TYPE", new Admin(true));
        startActivity(signIn);
    }

    // copied from firebase documentation
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // if user that's logged in is not admin
            if(!Objects.equals(currentUser.getEmail(), adminEmail)) {
                getUser(currentUser);
            } else if (Objects.equals(currentUser.getEmail(), adminEmail)) { // if user that's logged in is admin
                loginAdmin();
            }
        }
    }

    /**
     * method takes in the current user using firebase authentication and finds their data in the
     * realtime database, creates a new user object of that users type, and updates ui to the home
     * page.
     * @param currentFirebaseUser the current firebase user signed into the device using firebase
     *                            authentication
     */
    private void getUser(FirebaseUser currentFirebaseUser){
        mDatabase = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("users");

        // gets the json object at the current firebase users uid in the realtime database
        mDatabase.child(currentFirebaseUser.getUid()).get()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        // if user not in realtime database or error finding them, Log error and show toast
                        Log.e("firebase", "cannot find user", task.getException());
                        Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show();
                    }else{
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        // create a hashmap out of the "json" like string that the firebase database get method returns
                        HashMap<String, String> userAttributes = getUserHashMap(String.valueOf(task.getResult().getValue()));
                        try {
                            // check if the user is banned or suspended
                            // -1 is banned, 0 is nothing
                            // any value >0 is the number of days the account is suspended
                            if ("client".equals(userAttributes.get("userType"))) {
                                updateUI(currentFirebaseUser, new ClientUser(userAttributes));
                            }else if("0".equals(userAttributes.get("accountStatus"))) {
                                // check user type and update ui with new user object of that type
                                if ("cook".equals(userAttributes.get("userType"))) {
                                    updateUI(currentFirebaseUser, new CookUser(userAttributes));
                                } else {
                                    Log.e("signIn", "failed to detemine user type, userType: " + userAttributes.get("userType"));
                                }
                            }else if ("-1".equals(userAttributes.get("accountStatus"))){
                                Toast.makeText(this, "Account banned", Toast.LENGTH_LONG).show();
                            }else{
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
                                Date localDate = new java.sql.Date(System.currentTimeMillis());
                                try {
                                    Date suspensionEnd = simpleDateFormat
                                                    .parse(Objects.requireNonNull
                                                            (userAttributes.get("suspensionEnd"))
                                                    );

                                    if(localDate.after(suspensionEnd)){

                                        HashMap<String,Object> newUserAttributes = new HashMap<>();

                                        newUserAttributes.put("suspensionEnd", "NULL");
                                        newUserAttributes.put("accountStatus", "0");

                                        mDatabase.child(currentFirebaseUser.getUid())
                                                .updateChildren(newUserAttributes);
                                        getUser(currentFirebaseUser);
                                    }else{
                                        Toast.makeText(this,
                                                "Account suspended until " + userAttributes.get("suspensionEnd"),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }catch(ParseException parseException){
                                    parseException.printStackTrace();
                                }
                            }
                        }catch(NullPointerException ex){
                            Log.e("signIn", Arrays.toString(ex.getStackTrace()));
                            // TODO toast
                        }
                    }
                });
    }

    /**
     * creates a hashmap out of the "json" like string inputted
     * "json" like => { attribute=value, attribute=value, .. , etc }
     * @param userAttributes "json" like string with the users attributes
     * @return a hashmap of the users attribute names as keys and the attribute values as values
     */
    private HashMap<String, String> getUserHashMap(String userAttributes){
        // initialize new hashmap
        HashMap<String, String> userAttributesHash = new HashMap<>();

        // remove all the unnecessary space and characters that are a result of the string being "json" like
        String[] splitOne =
                userAttributes
                        .replace("{", "")
                        .replace("}", "")
                        .replace(" ", "")
                        // split the string by "," in order to get an array of attributes and their values
                        .split(",");

        // loop through each string and split by "=" left(0) is attribute, right(1) is value
        for(String index : splitOne){
            userAttributesHash.put(index.split("=")[0], index.split("=")[1]);
        }

        // return the completed hashmap
        return userAttributesHash;
    }

    /**
     * update the android ui to start a new activity and passes the currentUser object onto the next
     * activity
     * @param currentFirebaseUser the current user logged in through firebase authentication
     * @param currentUser the current user logged in as a user object with all the users attributes
     *                    and their values
     */
    private void updateUI(FirebaseUser currentFirebaseUser, User currentUser){
        // trying to update ui with null values will refresh the page
        if (currentFirebaseUser == null){
            finish();
            startActivity(getIntent());
            return;
        }
        user = currentUser;
        userId = currentFirebaseUser.getUid();
        Intent signIn = new Intent(this, ClientHomePage.class);
        if("cook".equals(currentUser.getUserType())){
            signIn = new Intent(this, CookHomePage.class);
        }
        signIn.putExtra("TYPE", currentUser);
        startActivity(signIn);
        finish();
    }

    /**
     * Checks if the user signing in is an admin or a regular user
     * @param email email user used to sign in
     * @return returns true if the email used is the admin email for the app
     */
    private boolean isAdmin(String email){
        Log.d("Admin email", String.valueOf(email.equals(adminEmail)));
        return email.equals(adminEmail);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent notifs = new Intent(this, NotificationService.class );
        notifs.putExtra("USER", user);
        notifs.putExtra("ID", userId);
        startService(notifs) ;
    }
}