package com.mealer.ui.ui.dashboard;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import java.util.Objects;

//TODO create complaint class
public class DashboardFragment extends Fragment {

    private final String TAG = "DashboardFragment";

    private FragmentComplaintsBinding binding;
    private DatabaseReference mData;
    private DatabaseReference banRef;
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

        banRef = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("users");

        complaintsNum = binding.complaintsNum;
        subject = binding.subject;
        user = binding.complaintAbout;
        description = binding.complaintDescription;

        ignore = binding.ignoreButton;
        refresh = binding.refreshButton;
        suspend = binding.suspendButton;
        ban = binding.banButton;

        getComplaints();
        // sets onClick listeners for all buttons
        refresh.setOnClickListener(onClick -> getComplaints());
        ban.setOnClickListener(banUser -> displayNewComplaint("-1"));
        suspend.setOnClickListener(suspend -> openPopup(this.getView()));
        ignore.setOnClickListener(displayNew-> displayNewComplaint(null));

        return root;
    }

    /**
     * Opens a small popup window where the admin can input the length of the cooks suspension
     * @param view androidStudio view to show popup
     */
    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    public void openPopup(View view){
        //TODO fix popup, not showing properly in app
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        View popup = inflater.inflate(R.layout.suspension_popup, null);
        popup.setBackgroundColor(Color.BLACK);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
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
            String suspensionLengthDays = suspensionLength.getText().toString();
            if("Months".equals(monthDayChoice.getSelectedItem())){
                /* the db stores suspension in days, so if admin inputted suspension in months,
                multiply by 30 to get how many days to suspend */
                suspensionLengthDays = String.valueOf(Integer.parseInt(suspensionLengthDays)*30);
            }
            popupWindow.dismiss();
            displayNewComplaint(suspensionLengthDays);
        });
    }

    /**
     * Actions the previous complaint and displays the next complaint in the list to the admin
     * @param status how to set the account status of the user of the actioned complaint
     *               "-1" => banned
     *               "null" => ignore the complaint
     *               "x">0 => the number of days the user will be suspended
     */
    private void displayNewComplaint(String status){
        if(status != null) {
            HashMap<String, Object> update = new HashMap<>();
            update.put("accountStatus", status);
            banRef.child(user.getText().toString()).updateChildren(update);
        }
        //mData.child(complaintList.get(index)).removeValue();
        try {
            displayComplaint(complaintList.get(++index));
        }catch (IndexOutOfBoundsException i){
            subject.setText("NULL");
            user.setText("NULL");
            description.setText("No complaints");
        }
    }

    /**
     * gets a list of all the complaints in the db
     */
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
                //displayComplaint(complaintList.get(index));
                //TODO test :
                displayComplaint(complaintList.get(index));
            }else{
                Log.e("firebaseDatabase", "no complaint", task.getException());
            }
        });
    }


    // test method for trying to pull data differently
    private void displayComplaint(String path){
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}