package com.mealer.ui.ui.menu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mealer.app.menu.Menu;
import com.mealer.app.menu.MenuItem;
import com.mealer.ui.OnFragmentInteractionListener;
import com.mealer.ui.R;
import com.mealer.ui.databinding.FragmentMenuItemDetailsBinding;

import java.io.IOException;

public class MenuNewFragment extends Fragment {

    private final int fragmentNavId = R.id.action_navigation_new_menu_item_to_navigation_cook_menu;

    private FragmentMenuItemDetailsBinding binding;
    private OnFragmentInteractionListener mListener;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Button addItem;

    private ImageView itemImage;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    private EditText name;
    private EditText description;
    private EditText calories;
    private EditText ingredients;
    private CheckBox isActive;
    private Button delete;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentMenuItemDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        root.setBackgroundColor(Color.parseColor("#FEFAE0"));

        addItem = binding.addNewItem;

        name = binding.itemNameText;
        description = binding.itemDescriptionText;
        calories = binding.itemCaloriesText;
        ingredients = binding.itemIngredients;
        isActive = binding.isActiveBox;
        delete = binding.delete;
        itemImage = binding.mealImage;

        // get the Firebase storage reference
        storage = FirebaseStorage.getInstance("gs://mealer-app-58f99.appspot.com");
        storageReference = storage.getReference();

        delete.setVisibility(View.GONE);

        itemImage.setOnClickListener(x->selectImage());

        // on add item click, create new MenuItem, add to menu, and return to menuFragment
        addItem.setOnClickListener(onCLick -> {
            Bundle args = getArguments();
            Menu userMenu = args.getParcelable("MENU");

            MenuItem newItem = new MenuItem(
                    name.getText().toString(),
                    description.getText().toString(),
                    calories.getText().toString(),
                    ingredients.getText().toString().replace(" ", ""),
                    isActive.isChecked()
            );

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            userMenu.addNewMenuItem(newItem);
            uploadImage(currentUser.getUid(), newItem.getItemId());
            updateUI();
        });

        return root;
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
                                        .getBitmap(this.getContext().getContentResolver(), filePath);
                                itemImage.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                // Log the exception
                                e.printStackTrace();
                            }
                        }
                    });

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

    /**
     * uploads the image, that the user signing up selected, to the firebase storage with
     * the users generated id as the name of the image
     * @param uId user id of the user signing up
     */
    private void uploadImage(String uId, String mealId) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this.getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference
                    .child("images/" + uId + "/" + mealId);

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
            // percentage on the dialog box
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        // Dismiss dialog
                        progressDialog.dismiss();
                        Toast.makeText(this.getContext(), "Image Uploaded!!",
                                Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast.makeText(this.getContext(),
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

    /**
     * gets the mListener object from the fragments context in order to be able to return to
     * previous fragment and get the complaint info passed to this fragment
     * @param context fragment context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void updateUI(){
        mListener.changeFragment(null, fragmentNavId);
    }
}
