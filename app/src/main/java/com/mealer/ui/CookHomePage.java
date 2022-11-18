package com.mealer.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mealer.ui.databinding.ActivityCookHomePageBinding;
import com.mealer.ui.ui.menu.MenuNewFragment;

import java.io.IOException;


public class CookHomePage extends AppCompatActivity implements OnFragmentInteractionListener, UploadImage{

    private ActivityCookHomePageBinding binding;
    NavController navController;

    protected FirebaseStorage storage;
    protected StorageReference storageReference;
    protected Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        binding = ActivityCookHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get the Firebase storage reference
        storage = FirebaseStorage.getInstance("gs://mealer-app-58f99.appspot.com");
        storageReference = storage.getReference();

        BottomNavigationView navView = findViewById(R.id.cook_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_cook_menu, R.id.navigation_notifications, R.id.navigation_account)
                .build();

        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_cook_home_page);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.cookNavView, navController);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void changeFragment(Bundle args, int id) {
        navController.navigate(id, args);
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
                                System.out.println(filePath);
                            }
/*
                            try {
                                // Setting image on image view using Bitmap
                                Bitmap bitmap = MediaStore.Images.Media
                                        .getBitmap(this.getContentResolver(), filePath);
                                MenuNewFragment.itemImage.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                // Log the exception
                                e.printStackTrace();
                            }

 */
                        }
                    });

    // image upload methods from
    // https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/
    // Select Image method
    @Override
    public void selectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent imageIntent = new Intent();
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        imageSelectedResult.launch(
                Intent.createChooser(imageIntent, "Select Image"));
    }

    /**
     * uploads the image, that the user signing up selected, to the firebase storage with
     * the users generated id as the name of the image
     * @param uId user id of the user signing up
     */
    @Override
    public void uploadImage(String uId, String mealId) {
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference
                    .child("images/").child(uId + "/" + mealId);

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