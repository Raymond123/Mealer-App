package com.mealer.ui.ui.search;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SearchFragment extends Fragment {

    private final String TAG = "SearchFragment";
    private final int navToDetails = R.id.action_navigation_client_home_to_navigation_menu_item_details;

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
    private HashMap<String, String> itemToCook;
    private SearchAdapter searchAdapter;

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


        results = new ArrayList<>();
        searchAdapter = new SearchAdapter(context, results);

        searchResult.setLayoutManager(new LinearLayoutManager(context));
        searchResult.setAdapter(searchAdapter);
        searchResult.addOnItemTouchListener(menuItemDetails);

        menuData.addValueEventListener(defaultEvent);
        searchButton.setOnClickListener(onCLick -> {
            if(!searchText.getText().toString().isEmpty()) {
                menuData.removeEventListener(defaultEvent);
                menuData.addValueEventListener(searchEvent);
            }else{
                menuData.addValueEventListener(defaultEvent);
            }
        });

        return root;
    }

    ValueEventListener searchEvent = new ValueEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            results.clear();
            itemToCook = new HashMap<>();

            String search = searchText.getText().toString();
            String[] keywords = search.toLowerCase(Locale.ROOT).split(" ");

            for(DataSnapshot child : snapshot.getChildren()){
                try {
                    getCook(child.getKey(), cook -> {
                        if(!"0".equals(cook.getAccountStatus())){
                            Log.d(TAG, "cook suspended");
                        }else{
                            for (DataSnapshot grandchild : child.child("active").getChildren()) {
                                MenuItem item = grandchild.getValue(MenuItem.class);
                                for (String word : keywords) {
                                    if (item != null && item.getItemName().toLowerCase(Locale.ROOT).contains(word)) {
                                        itemToCook.put(item.getItemId(), child.getKey());
                                        results.add(item);
                                        searchAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    });
                }catch (NullPointerException nullPointerException){
                    Log.e(TAG, Arrays.toString(nullPointerException.getStackTrace()));
                }
            }

            //searchResult.setAdapter(new SearchAdapter(context, results));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ValueEventListener defaultEvent = new ValueEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            results.clear();
            itemToCook = new HashMap<>();
            for(DataSnapshot child : snapshot.getChildren()){
                try {
                    getCook(child.getKey(), cook -> {
                        if(!"0".equals(cook.getAccountStatus())){
                            Log.d(TAG, "cook suspended");
                        }else{
                            try {
                                DataSnapshot grandchild = child.child("active").getChildren().iterator().next();
                                MenuItem item = grandchild.getValue(MenuItem.class);
                                if(item != null) {
                                    itemToCook.put(item.getItemId(), child.getKey());
                                    results.add(item);
                                    searchAdapter.notifyDataSetChanged();
                                }
                            } catch (NoSuchElementException noElem) {
                                Log.e(TAG, "no such element");
                            }
                        }
                    });
                }catch (NullPointerException nullPointerException){
                    Log.e(TAG, "null cook");
                }
            }

            //searchResult.setAdapter(new SearchAdapter(context, results));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    /**
     * @param cookId the cook id to check if is suspended
     * @param listener cookListener callback implementation
     */
    private void getCook(String cookId, CookListener listener){
        //AtomicBoolean suspended = new AtomicBoolean(false);
        cookRef.child(cookId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                CookUser cookUser = task.getResult().getValue(CookUser.class);
                if(cookUser!=null && "0".equals(cookUser.getAccountStatus())){
                    // suspended.set(true);
                    listener.onCookRecieved(cookUser);
                }
            }else{
                Log.d(TAG, "Error getting cook, " + task.getException());
            }
        });
        //return suspended.get();
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
                            MenuItem item = results.get(position);
                            args.putParcelable("ITEM", item);
                            args.putString("COOK", itemToCook.get(item.getItemId()));
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
