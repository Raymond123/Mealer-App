package com.mealer.ui.ui.search;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mealer.app.CookUser;
import com.mealer.app.menu.Menu;
import com.mealer.app.menu.MenuItem;
import com.mealer.ui.OnFragmentInteractionListener;
import com.mealer.ui.databinding.FragmentClientHomeBinding;
import com.mealer.ui.ui.RecyclerItemClickListener;
import com.mealer.ui.ui.menu.MenuAdapter;
import com.mealer.ui.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SearchFragment extends Fragment {

    private final String TAG = "SearchFragment";
    private final int navToDetails = R.id.action_navigation_client_home_to_navigation_menu_item_details;
    private Object lock = new Object();
    AtomicReference<CookUser> cook = new AtomicReference<>();

    private FragmentClientHomeBinding binding;
    private DatabaseReference menuData;
    private DatabaseReference cookRef;
    private FirebaseUser currentUser;
    private OnFragmentInteractionListener mListener;

    private Context context;

    private EditText searchText;
    private Button searchButton;
    private RecyclerView searchResult;

    private ArrayList<MenuItem> results;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentClientHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        root.setBackgroundColor(Color.parseColor("#FEFAE0"));

        menuData = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("menus");
        cookRef = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        context = this.getContext();

        searchButton = binding.searchMenu;
        searchText = binding.searchText;
        searchResult = binding.searchResult;

        setDefaultMenuSearch();
        searchButton.setOnClickListener(onCLick -> search(searchText.getText().toString()));

        return root;
    }

    private void search(String searchText){
        if(searchText == null || searchText.equals("")){
            setDefaultMenuSearch();
            return;
        }

        String[] keywords = searchText.toLowerCase(Locale.ROOT).split(" ");
        menuData.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                results = new ArrayList<>();
                DataSnapshot data = task.getResult();
                for(DataSnapshot child : data.getChildren()){
                    getCook(child.getKey());
                    try {
                        if (!cook.get().getAccountStatus().equals("0")) {
                            continue;
                        }
                        for (DataSnapshot grandchild : child.child("active").getChildren()) {
                            MenuItem item = grandchild.getValue(MenuItem.class);
                            for (String word : keywords) {
                                if (item != null && item.getItemName().toLowerCase(Locale.ROOT).contains(word)) {
                                    results.add(item);
                                }
                            }
                        }
                    }catch (NullPointerException nullPointerException){
                        Log.e(TAG, Arrays.toString(nullPointerException.getStackTrace()));
                    }
                }

                searchResult.setLayoutManager(new LinearLayoutManager(context));
                searchResult.setAdapter(new MenuAdapter(context, results));
                searchResult.addOnItemTouchListener(menuItemDetails);
            }else{
                Log.e(TAG, "Error getting menus");
            }
        });
    }

    /**
     *
     */
    private void setDefaultMenuSearch(){
        // default view when loading search, will display first active menu item for each cook
        menuData.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                results = new ArrayList<>();
                DataSnapshot data = task.getResult();
                for(DataSnapshot child : data.getChildren()){
                    getCook(child.getKey());
                    try {
                        if (!cook.get().getAccountStatus().equals("0")) {
                            Log.d(TAG, "cook suspended");
                            continue;
                        }
                        try {
                            DataSnapshot grandchild = child.child("active").getChildren().iterator().next();
                            MenuItem item = grandchild.getValue(MenuItem.class);
                            // can use cook.get() (is a CookUser class) to display cook details,
                            // once text boxes are added to use them
                            results.add(item);
                        } catch (NoSuchElementException noElem) {
                            Log.e(TAG, "no such element");
                        }
                    }catch (NullPointerException nullPointerException){
                        Log.e(TAG, "null cook");
                    }
                }

                searchResult.setLayoutManager(new LinearLayoutManager(context));
                searchResult.setAdapter(new MenuAdapter(context, results));
                searchResult.addOnItemTouchListener(menuItemDetails);
            }else{
                Log.e(TAG, "Error getting menus");
            }
        });
    }

    /**
     * @param cookId the cook id to check if is suspended
     * @return the boolean value of whether or not the cook is suspended
     */
    private void getCook(String cookId){
        cookRef.child(cookId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                CookUser cookUser = task.getResult().getValue(CookUser.class);
                cook.set(cookUser);
            }else{
                Log.d(TAG, "Error getting cook, " + task.getException());
            }
        });
    }

    /**
     * the onTouch listener for the activeMenu item
     * listens for when an item is touched, and opens the details fragment
     */
    private final RecyclerView.OnItemTouchListener menuItemDetails =
            new RecyclerItemClickListener(context, this.searchResult,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Bundle args = new Bundle();
                            args.putParcelable("ITEM", results.get(position));
                            updateUI(args, navToDetails);
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {

                        }
                    });

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

    private void updateUI(Bundle args, int id) {
        mListener.changeFragment(args, id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}
