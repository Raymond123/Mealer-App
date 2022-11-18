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
import android.widget.ImageView;

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

        // gets Menu and MenuItem from argument bundle passed in navigation
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

        // on save item button set new item attrbiutes and updateMenu, then return to menuFragment
        saveItem.setOnClickListener(x->{
            menuItem.setItemName(name.getText().toString());
            menuItem.setItemDescription(description.getText().toString());
            menuItem.setCalories(calories.getText().toString());
            menuItem.setMainIngredients(ingredients.getText().toString());

            // if the state of the item active check box is different to the state of the item,
            // move the item to the other Map in the menu
            if(menuItem.isActive() != isActive.isChecked()){
                userMenu.moveItem(menuItem);
            }

            userMenu.updateMenu();
            updateUI();
        });

        deleteItem.setOnClickListener(x->{
            userMenu.removeMenuItem(menuItem);
            updateUI();
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
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void updateUI(){
        mListener.changeFragment(null, fragmentNavId);
    }
}
