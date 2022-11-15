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

public class MenuNewFragment extends Fragment {

    private final int fragmentNavId = R.id.action_navigation_new_menu_item_to_navigation_cook_menu;

    private FragmentMenuItemDetailsBinding binding;
    private OnFragmentInteractionListener mListener;

    private Button addItem;

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

        delete.setVisibility(View.GONE);

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
            ValidateMenu validateMenu = new ValidateMenu(newItem);
            if(validateMenu.validateAll(this.getContext())){
                userMenu.addNewMenuItem(newItem);
                System.out.println(userMenu);
                updateUI();
            }
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
