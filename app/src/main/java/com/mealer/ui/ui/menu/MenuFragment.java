package com.mealer.ui.ui.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mealer.app.CookUser;
import com.mealer.app.User;
import com.mealer.app.menu.Menu;
import com.mealer.app.menu.MenuItem;
import com.mealer.ui.OnFragmentInteractionListener;
import com.mealer.ui.R;
import com.mealer.ui.databinding.FragmentComplaintsListBinding;
import com.mealer.ui.databinding.FragmentCookHomeBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuFragment extends Fragment {

    private final String TAG = "MenuFragment";
    private final int navToNew = R.id.action_navigation_cook_menu_to_navigation_new_menu_item;
    private final int navToEditMenu = R.id.action_navigation_cook_menu_to_navigation_edit_menu;
    private final int navToEdit = R.id.action_navigation_cook_menu_to_navigation_edit_menu_item;
    private final int navToDelete = R.id.action_navigation_cook_menu_to_navigation_delete_menu_item;

    private FragmentCookHomeBinding binding;
    private DatabaseReference menuData;
    private OnFragmentInteractionListener mListener;

    private Button editMenu;

    RecyclerView activeMenu;
    RecyclerView inactiveMenu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentCookHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        root.setBackgroundColor(Color.parseColor("#FEFAE0"));

        menuData = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("menus");

        editMenu = binding.editMenu;

        editMenu.setOnClickListener(x->popup(this.getView()));

        activeMenu = binding.activeMenu;
        inactiveMenu = binding.inactiveMenu;

        Context context = this.getContext();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Menu userMenu = new Menu(menuData.child(currentUser.getUid()));

        MenuItem temp = new MenuItem("test",
                "descriptdsfsdion", "calsfdfories",
                "ingresdfsdients", false);

        userMenu.addNewMenuItem(temp);
        userMenu.moveItem(temp);
        userMenu.addNewMenuItem(new MenuItem("nafsfme",
                "descrifsfption", "calofsfries",
                "ingredsfsients", false));

        Collection<MenuItem> activeMenuItemCollection = userMenu.getActiveMenu().values();
        List<MenuItem> activeMenuItems = new ArrayList<>(activeMenuItemCollection);

        Collection<MenuItem> inactiveMenuItemCollection = userMenu.getInactiveMenu().values();
        List<MenuItem> inactiveMenuItems = new ArrayList<>(inactiveMenuItemCollection);

        activeMenu.setLayoutManager(new LinearLayoutManager(context));
        activeMenu.setAdapter(new MenuAdapter(context, activeMenuItems));

        inactiveMenu.setLayoutManager(new LinearLayoutManager(context));
        inactiveMenu.setAdapter(new MenuAdapter(context, inactiveMenuItems));

        return root;
    }

    private HashMap<String, MenuItem> castMap(HashMap<String, Object> map){
        HashMap<String, MenuItem> newMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if(entry.getValue() instanceof String){
                newMap.put(entry.getKey(), (MenuItem) entry.getValue());
            }
        }
        return newMap;
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

    @SuppressLint("MissingInflatedId")
    private void popup(View view){
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        View popup = inflater.inflate(R.layout.menu_popup, null);
        popup.setBackgroundColor(Color.BLACK);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it

        final PopupWindow popupWindow = new PopupWindow(popup, width, height, focusable);

        Button newItem = popup.findViewById(R.id.newMenuItem);
        Button editMenu = popup.findViewById(R.id.editActiveMenu);
        Button editItem = popup.findViewById(R.id.editMenuItem);
        Button removeItem = popup.findViewById(R.id.deleteMenuItem);

        newItem.setOnClickListener(onClick -> updateUI(navToNew, popupWindow));
        editMenu.setOnClickListener(onClick -> updateUI(navToEditMenu, popupWindow));
        editItem.setOnClickListener(onClick -> updateUI(navToEdit, popupWindow));
        removeItem.setOnClickListener(onClick -> updateUI(navToDelete, popupWindow));

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void updateUI(int id, PopupWindow popupWindow) {
        mListener.changeFragment(null, id);
        popupWindow.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
