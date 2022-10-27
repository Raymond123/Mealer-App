package com.mealer.ui.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mealer.ui.databinding.FragmentComplaintsBinding;

import org.w3c.dom.Text;

import java.util.HashMap;

public class DashboardFragment extends Fragment {

    private FragmentComplaintsBinding binding;
    private DatabaseReference mData;
    private FirebaseAuth mAuth;

    TextView subject;
    TextView user;
    TextView description;

    Button ignore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentComplaintsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.getRoot().setBackgroundColor(Color.parseColor("#FEFAE0"));

        mAuth = FirebaseAuth.getInstance();

        mData = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("complaints");

        subject = binding.subject;
        user = binding.subject2;
        description = binding.complaintDescription;

        ignore = binding.button3;

        String[] list = {"0001", "0002"};

        displayComplaint(list[0]);

        ignore.setOnClickListener(displayNew->{
            mData.child(list[0]).removeValue();
            displayComplaint(list[1]);
        });

        return root;
    }

    private HashMap<String, String> parseComplaint(String complaintData){
        HashMap<String, String> complaintParsed = new HashMap<>();
        String[] splitOne =
                complaintData.replace("{", "")
                        .replace("}", "")
                        .replace(" ", "")
                        // split the string by "," in order to get an array of attributes and their values
                        .split(",");

        for(String index : splitOne){
            String[] temp = index.split("=");
            complaintParsed.put(temp[0], temp[1]);
        }

        return complaintParsed;
    }

    private void displayComplaint(String path){
        mData.child(path).get().addOnCompleteListener(c->{
            if(c.isSuccessful()){
                Log.d("firebaseDatabase", "found complaint", c.getException());
                HashMap<String, String> complaintData = parseComplaint(String.valueOf(c.getResult().getValue()));

                subject.setText(complaintData.get("subject"));
                user.setText(complaintData.get("user"));
                description.setText(complaintData.get("description"));
            }else{
                Log.e("firebaseDatabase", "no complaint", c.getException());
            }
        });
    }


    private void loadNewComplaint(){

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}