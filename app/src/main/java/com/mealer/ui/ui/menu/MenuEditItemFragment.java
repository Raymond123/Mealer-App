package com.mealer.ui.ui.menu;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mealer.app.menu.Menu;
import com.mealer.app.menu.MenuItem;
import com.mealer.ui.OnFragmentInteractionListener;
import com.mealer.ui.R;
import com.mealer.ui.databinding.FragmentMenuItemDetailsBinding;

public class MenuEditItemFragment extends Fragment {

    private final int fragmentNavId = R.id.action_navigation_edit_menu_item_to_navigation_cook_menu;

    private FragmentMenuItemDetailsBinding binding;
    private OnFragmentInteractionListener mListener;

    private Button saveItem;
    private Button deleteItem;
    private EditText name;
    private EditText description;
    private EditText calories;
    private EditText ingredients;
    private CheckBox isActive;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentMenuItemDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        root.setBackgroundColor(Color.parseColor("#FEFAE0"));

        Bundle args = getArguments();
        Menu userMenu = args.getParcelable("MENU");
        MenuItem menuItem = args.getParcelable("ITEM");

        saveItem = binding.addNewItem;
        deleteItem = binding.delete;

        name = binding.itemNameText;
        description = binding.itemDescriptionText;
        calories = binding.itemCaloriesText;
        ingredients = binding.itemIngredients;
        isActive = binding.isActiveBox;

        if(menuItem.isActive()){
            deleteItem.setEnabled(false);
        }

        name.setText(menuItem.getItemName());
        description.setText(menuItem.getItemDescription());
        calories.setText(menuItem.getCalories());
        ingredients.setText(menuItem.getMainIngredients());
        isActive.setChecked(menuItem.isActive());

        saveItem.setOnClickListener(x->{
            menuItem.setItemName(name.getText().toString());
            menuItem.setItemDescription(description.getText().toString());
            menuItem.setCalories(calories.getText().toString());
            menuItem.setMainIngredients(ingredients.getText().toString());

            if(menuItem.isActive() != isActive.isChecked()){
                userMenu.moveItem(menuItem);
            }
            ValidateMenu validateMenu = new ValidateMenu(menuItem);
            if(validateMenu.validateAll(this.getContext())){
                userMenu.updateMenu();
                updateUI();
            }
        });

        deleteItem.setOnClickListener(x->{
            userMenu.removeMenuItem(menuItem);
            updateUI();
        });

        return root;
    }

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
