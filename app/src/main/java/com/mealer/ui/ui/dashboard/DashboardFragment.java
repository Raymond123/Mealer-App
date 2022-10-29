package com.mealer.ui.ui.dashboard;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mealer.app.Complaint;
import com.mealer.app.CookUser;
import com.mealer.app.User;
import com.mealer.ui.R;
import com.mealer.ui.databinding.FragmentComplaintsBinding;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//TODO create complaint class
public class DashboardFragment extends Fragment {

    private final String TAG = "DashboardFragment";

    private FragmentComplaintsBinding binding;
    private DatabaseReference mData;
    private FirebaseAuth mAuth;
    private Integer index = 0;
    private ArrayList<String> complaintList;

    TextView complaintsNum;
    TextView subject;
    TextView user;
    TextView description;

    Button refresh;
    Button ignore;
    Button suspend;
    Button ban;

    ValueEventListener userListener =  new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            User user = snapshot.getValue(CookUser.class);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", error.toException());
        }
    };

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

        complaintsNum = binding.complaintsNum;
        subject = binding.subject;
        user = binding.complaintAbout;
        description = binding.complaintDescription;

        ignore = binding.ignoreButton;
        refresh = binding.refreshButton;
        suspend = binding.suspendButton;
        ban = binding.banButton;

        getComplaints();
        refresh.setOnClickListener(onClick -> getComplaints());

        ban.setOnClickListener(banUser -> {
            DatabaseReference banRef = FirebaseDatabase
                    .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                    .getReference("users");
            //banRef.child(user.getText().toString()).addValueEventListener(userListener);

            banRef.child(user.getText().toString()).removeValue();
            mData.child(complaintList.get(index)).removeValue();
            displayComplaint(complaintList.get(++index));
        });

        suspend.setOnClickListener(suspend ->{
            openPopup(this.getView());
        });

        ignore.setOnClickListener(displayNew->{
            mData.child(complaintList.get(index)).removeValue();
            displayComplaint(complaintList.get(++index));
        });

        return root;
    }

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    public void openPopup(View view){
        LayoutInflater inflater = getLayoutInflater();
        View popup = inflater.inflate(R.layout.suspension_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it

        final PopupWindow popupWindow = new PopupWindow(popup, width, height, focusable);

        Spinner monthDayChoice = popup.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(popup.getContext(),
                R.array.monthDayChoice, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        monthDayChoice.setAdapter(adapter);

        Button completeSuspension = popup.findViewById(R.id.completeSuspension);
        EditText suspensionLength = popup.findViewById(R.id.suspensionLength);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popup.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });

        completeSuspension.setOnClickListener(suspend -> {
            //TODO suspend user for the length specified
        });

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

    private void getComplaints(){
        complaintList = new ArrayList<>();
        mData.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String[] split = String.valueOf(task.getResult().getValue())
                        .replace("{", "")
                        .replace("}", "")
                        .replace(" ", "")
                        // split the string by "," in order to get an array of attributes and their values
                        .split(",");

                for(String title : split){
                    String[] split2 = title.split("=");
                    if(split2.length == 3){
                        complaintList.add(split2[0]);
                    }
                }

                complaintsNum.setText(String.valueOf(complaintList.size()));
                displayComplaint(complaintList.get(index));
                //TODO test : testDisplay(complaintList.get(index));
            }else{
                Log.e("firebaseDatabase", "no complaint", task.getException());
            }
        });
    }

    // test method for trying to pull data differently
    private void testDisplay(String path){
        ValueEventListener complaintListener =  new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Complaint complaint = snapshot.getValue(Complaint.class);

                subject.setText(complaint.getSubject());
                user.setText(complaint.getCookID());
                description.setText(complaint.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        };
        mData.child(path).addValueEventListener(complaintListener);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}