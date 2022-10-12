package com.mealer.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mealer.app.CookUser;
import com.mealer.app.User;

import java.io.IOException;
import java.util.UUID;

public class SignUpPageCook extends AppCompatActivity {

    private final String TAG = "SignUpPageClient";

    EditText fName, lName, email, password, confirmPassword, address, description;
    Button createAccount, selectImage;
    ImageView imagePreview;

    private FirebaseAuth mAuth;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page_cook);

        mAuth = FirebaseAuth.getInstance();

        fName = findViewById(R.id.createAccountFirstName);
        lName = findViewById(R.id.createAccountLastName);
        email = findViewById(R.id.createAccountEmail);
        password = findViewById(R.id.createAccountPassword);
        confirmPassword = findViewById(R.id.createAccountPassword2);
        address = findViewById(R.id.createAccountAddress);
        description = findViewById(R.id.userDescriptionInput);

        createAccount = findViewById(R.id.signupButton);
        selectImage = findViewById(R.id.selectImageButton);
        imagePreview = findViewById(R.id.imageUploadPreview);

        // get the Firebase storage reference
        storage = FirebaseStorage.getInstance("gs://mealer-app-58f99.appspot.com");
        storageReference = storage.getReference();

        selectImage.setOnClickListener(v->selectImage());

        createAccount.setOnClickListener(v->{
            String passwordText = password.getText().toString();
            if(passwordText.equals(confirmPassword.getText().toString())) {
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), passwordText)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Log.d(TAG, "createUserWithEmailSuccess");
                                FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();
                                assert currentFirebaseUser != null;
                                User currentUser = new CookUser(fName.getText().toString(), lName.getText().toString(),
                                        email.getText().toString(), address.getText().toString(), description.getText().toString(),
                                        currentFirebaseUser.getUid(), "cook");
                                uploadImage(currentFirebaseUser.getUid());
                                updateUI(currentFirebaseUser, currentUser);
                            }else{
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                //PopupWindow failWindow = new PopupWindow(View, width, height, true);
                                //failWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                                Toast.makeText(SignUpPageCook.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null, null);
                            }
                        });
            }
        });

    }

    // Activity on result
    // https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative

    private final ActivityResultLauncher<Intent> imageSelectedResult =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            if(data != null && data.getData() != null) {
                                filePath = data.getData();
                            }

                            try {
                                // Setting image on image view using Bitmap
                                Bitmap bitmap = MediaStore.Images.Media
                                        .getBitmap(getContentResolver(), filePath);
                                imagePreview.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                // Log the exception
                                e.printStackTrace();
                            }
                        }
                    });

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
            return;
        }

        currentFirebaseUser.sendEmailVerification()
                .addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        Toast.makeText(this,
                                "Verification email sent",
                                Toast.LENGTH_LONG).show();

                        Intent signIn = new Intent(SignUpPageCook.this, UserHomePage.class);
                        signIn.putExtra("TYPE", currentUser);
                        startActivity(signIn);
                    }else{
                        Toast.makeText(this,
                                "Error",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }


    // image upload methods from
    // https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/
    // Select Image method
    private void selectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imageSelectedResult.launch(
                Intent.createChooser(
                        intent, "Select Image"));
    }

    // UploadImage method
    private void uploadImage(String uId) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference
                    .child("images/" + uId);

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
            // percentage on the dialog box
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                                // Image uploaded successfully
                                // Dismiss dialog
                                progressDialog.dismiss();
                                Toast.makeText(this, "Image Uploaded!!",
                                                Toast.LENGTH_SHORT).show();
                            })
                    .addOnFailureListener(e -> {
                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast.makeText(this,
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                                double progress = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage(
                                        "Uploaded " + (int)progress + "%");
                            });
        }
    }
}