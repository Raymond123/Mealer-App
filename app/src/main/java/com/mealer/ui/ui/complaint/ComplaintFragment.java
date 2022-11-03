package com.mealer.ui.ui.complaint;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mealer.app.Complaint;
import com.mealer.app.CookUser;
import com.mealer.app.User;
import com.mealer.ui.OnFragmentInteractionListener;
import com.mealer.ui.R;
import com.mealer.ui.databinding.FragmentComplaintBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

//TODO create complaint class
public class ComplaintFragment extends Fragment {

    private final String TAG = "ComplaintFragment";

    private FragmentComplaintBinding binding;
    private DatabaseReference mData;
    private DatabaseReference banRef;
    private FirebaseAuth mAuth;
    private Integer index = 0;
    private ArrayList<String> complaintList;
    private OnFragmentInteractionListener mListener;

    TextView subject;
    TextView user;
    TextView description;

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
        /*ComplaintViewModel complaintViewModel =
                new ViewModelProvider(this).get(ComplaintViewModel.class);*/

        binding = FragmentComplaintBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.getRoot().setBackgroundColor(Color.parseColor("#FEFAE0"));

        mAuth = FirebaseAuth.getInstance();

        mData = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("complaints");

        banRef = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("users");

        subject = binding.subject;
        user = binding.complaintAbout;
        description = binding.complaintDescription;

        ignore = binding.ignoreButton;
        suspend = binding.suspendButton;
        ban = binding.banButton;

        displayComplaint();
        // sets onClick listeners for all buttons
        ban.setOnClickListener(banUser -> actionComplaint("-1"));
        suspend.setOnClickListener(suspend -> openPopup(this.getView()));
        ignore.setOnClickListener(displayNew-> actionComplaint(null));

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
            actionComplaint(suspensionLengthDays);
        });
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

    /**
     * Actions the previous complaint and returns to complaintsList
     * @param status how to set the account status of the user of the actioned complaint
     *               "-1" => banned
     *               "null" => ignore the complaint
     *               "x">0 => the number of days the user will be suspended
     */
    private void actionComplaint(String status){
        if(status != null) {
            HashMap<String, Object> update = new HashMap<>();
            update.put("accountStatus", status);

            String suspensionEnd = addDays(status);
            update.put("suspensionEnd", suspensionEnd);
            banRef.child(user.getText().toString()).updateChildren(update);
        }
        //mData.child(complaintList.get(index)).removeValue();
        mListener.changeFragment(null,1);
    }

    private String addDays(String daysToAddStr){
        int daysToAdd = Integer.parseInt(daysToAddStr);
        if(daysToAdd <= 0){
            return "NULL";
        }

        Date localDate = new java.sql.Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        Calendar calendar = Calendar.getInstance();
        try{
            calendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(localDate.toString())));
        }catch (ParseException parseException){
            parseException.printStackTrace();
        }
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);

        return simpleDateFormat.format(calendar.getTime());
    }


    /**
     * gets the argument passed to the fragment, and displays the information
     */
    private void displayComplaint(){
        Bundle args = getArguments();
        if(args!=null){
            Complaint currentComplaint = args.getParcelable("complaint");

            subject.setText(currentComplaint.getSubject());
            user.setText(currentComplaint.getCookID());
            description.setText(currentComplaint.getDescription());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}