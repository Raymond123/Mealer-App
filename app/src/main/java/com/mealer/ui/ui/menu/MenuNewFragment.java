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
import android.widget.TextView;
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
import com.mealer.ui.UploadImage;
import com.mealer.ui.databinding.FragmentMenuItemDetailsBinding;

import java.io.IOException;
import java.util.Objects;

public class MenuNewFragment extends Fragment {

    private final int fragmentNavId = R.id.action_navigation_new_menu_item_to_navigation_cook_menu;

    private FragmentMenuItemDetailsBinding binding;
    private OnFragmentInteractionListener mListener;
    private UploadImage uploadImage;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Button addItem;

    private ImageView itemImage;
    private TextView uploadText;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    private EditText name;
    private EditText description;
    private EditText calories;
    private EditText ingredients;
    private CheckBox isActive;
    private Button delete;

    private Bundle args;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentMenuItemDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        root.setBackgroundColor(Color.parseColor("#FEFAE0"));

        args = getArguments();
        setArguments(null);

        addItem = binding.addNewItem;

        name = binding.itemNameText;
        description = binding.itemDescriptionText;
        calories = binding.itemCaloriesText;
        ingredients = binding.itemIngredients;
        isActive = binding.isActiveBox;
        delete = binding.delete;

        itemImage = binding.mealImage;
        uploadText = binding.uploadImageText;

        // get the Firebase storage reference
        storage = FirebaseStorage.getInstance("gs://mealer-app-58f99.appspot.com");
        storageReference = storage.getReference();

        delete.setVisibility(View.GONE);
        uploadText.setVisibility(View.GONE);


        //itemImage.setOnClickListener(x->selectImage());

        // on add item click, create new MenuItem, add to menu, and return to menuFragment
        addItem.setOnClickListener(onCLick -> {
            //uploadImage.selectImage();
            if(args==null){
                updateUI();
            }else {
                Menu userMenu = args.getParcelable("MENU");

                MenuItem newItem = new MenuItem(
                        name.getText().toString(),
                        description.getText().toString(),
                        calories.getText().toString(),
                        ingredients.getText().toString().replace(" ", ""),
                        isActive.isChecked()
                );

                userMenu.addNewMenuItem(newItem);
                updateUI();
            }
        });

        return root;
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
            uploadImage = (UploadImage) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void updateUI(){
        mListener.changeFragment(null, fragmentNavId);
    }
}
