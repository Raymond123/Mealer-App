package com.mealer.ui.ui.complaint;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mealer.app.Complaint;
import com.mealer.ui.OnFragmentInteractionListener;
import com.mealer.ui.databinding.FragmentComplaintsListBinding;
import com.mealer.ui.ui.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ComplaintsListFragment extends Fragment {

    private final String TAG = "ComplaintsListFragment";

    private FragmentComplaintsListBinding binding;
    private DatabaseReference mData;
    private OnFragmentInteractionListener mListener;

    RecyclerView complaintsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentComplaintsListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.getRoot().setBackgroundColor(Color.parseColor("#FEFAE0"));

        mData = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("complaints");

        complaintsList = binding.complaintsView;
        Context context = this.getContext();

        List<Complaint> complaints = new ArrayList<>();
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Complaint complaint = ds.getValue(Complaint.class);
                    complaints.add(complaint);
                }
                complaintsList.setLayoutManager(new LinearLayoutManager(context));
                complaintsList.setAdapter(new ComplaintsList(context, complaints));
                complaintsList.addOnItemTouchListener(
                        new RecyclerItemClickListener(context, complaintsList,
                                new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Bundle args = new Bundle();
                                        args.putParcelable("complaint", complaints.get(position));
                                        updateUI(args);
                                    }

                                    @Override
                                    public void onLongItemClick(View view, int position) {

                                    }
                                })
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });

        return root;
    }

    /**
     * gets the mListener object from the fragments context in order to be able to return to
     * previous fragment and get the args info
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

    private void updateUI(Bundle args) {
        mListener.changeFragment(args,2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
