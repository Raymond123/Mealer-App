package com.mealer.ui.ui.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mealer.app.CookUser;
import com.mealer.app.User;
import com.mealer.app.menu.Menu;
import com.mealer.app.menu.MenuItem;
import com.mealer.ui.OnFragmentInteractionListener;
import com.mealer.ui.R;
import com.mealer.ui.databinding.FragmentComplaintsListBinding;
import com.mealer.ui.databinding.FragmentCookHomeBinding;
import com.mealer.ui.ui.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuFragment extends Fragment {

    private final String TAG = "MenuFragment";
    private final int navToNew = R.id.action_navigation_cook_menu_to_navigation_new_menu_item;
    private final int navToEdit = R.id.action_navigation_cook_menu_to_navigation_edit_menu_item;

    private FragmentCookHomeBinding binding;
    private DatabaseReference menuData;
    private FirebaseUser currentUser;
    private OnFragmentInteractionListener mListener;

    private Button editMenuPopup;
    private Context context;
    private Menu userMenu;
    private Bundle menuBundle;

    RecyclerView activeMenu;
    RecyclerView inactiveMenu;
    List<MenuItem> activeMenuItems;
    List<MenuItem> inactiveMenuItems;

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
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        editMenuPopup = binding.editMenu;

        activeMenu = binding.activeMenu;
        inactiveMenu = binding.inactiveMenu;

        context = this.getContext();
        menuData.child(currentUser.getUid()).addValueEventListener(getMenu);

        menuBundle = new Bundle();
        editMenuPopup.setOnClickListener(x->updateUI(menuBundle, navToNew));

        return root;
    }

    protected ValueEventListener getMenu = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            HashMap<String, Object> menu = new HashMap<>();
            for(DataSnapshot children : snapshot.getChildren()){
                HashMap<String, MenuItem> itemList = new HashMap<>();
                for(DataSnapshot grandChildren : children.getChildren()){
                    MenuItem item = grandChildren.getValue(MenuItem.class);

                    if("active".equals(children.getKey()))
                        item.setActive(true);
                    else
                        item.setActive(false);
                    item.setItemId(grandChildren.getKey());
                    itemList.put(grandChildren.getKey(), item);
                }
                menu.put(children.getKey(), itemList);
            }

            userMenu = new Menu(menu, menuData.child(currentUser.getUid()));
            menuBundle.putParcelable("MENU", userMenu);

            if(userMenu.getActiveMenu() != null) {
                Collection<MenuItem> activeMenuItemCollection = userMenu.getActiveMenu().values();
                activeMenuItems = new ArrayList<>(activeMenuItemCollection);

                activeMenu.setLayoutManager(new LinearLayoutManager(context));
                activeMenu.setAdapter(new MenuAdapter(context, activeMenuItems));
                activeMenu.addOnItemTouchListener(activeMenuItemDetails);
            }

            if(userMenu.getInactiveMenu() != null) {
                Collection<MenuItem> inactiveMenuItemCollection = userMenu.getInactiveMenu().values();
                inactiveMenuItems = new ArrayList<>(inactiveMenuItemCollection);

                inactiveMenu.setLayoutManager(new LinearLayoutManager(context));
                inactiveMenu.setAdapter(new MenuAdapter(context, inactiveMenuItems));
                inactiveMenu.addOnItemTouchListener(inactiveMenuItemDetails);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("VEL", error.toString());
        }
    };

    private final RecyclerView.OnItemTouchListener activeMenuItemDetails =
            new RecyclerItemClickListener(context, this.activeMenu,
                    new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Bundle args = new Bundle();
                    args.putParcelable("MENU", userMenu);
                    args.putParcelable("ITEM", activeMenuItems.get(position));
                    updateUI(args, navToEdit);
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            });

    private final RecyclerView.OnItemTouchListener inactiveMenuItemDetails =
            new RecyclerItemClickListener(context, this.inactiveMenu,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Bundle args = new Bundle();
                            args.putParcelable("MENU", userMenu);
                            args.putParcelable("ITEM", inactiveMenuItems.get(position));
                            updateUI(args, navToEdit);
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {

                        }
                    });


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

    private void updateUI(Bundle args, int id, PopupWindow popupWindow) {
        mListener.changeFragment(args, id);
        popupWindow.dismiss();
    }

    private void updateUI(Bundle args, int id) {
        mListener.changeFragment(args, id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
