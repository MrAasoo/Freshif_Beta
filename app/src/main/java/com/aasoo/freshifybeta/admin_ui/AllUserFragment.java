package com.aasoo.freshifybeta.admin_ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.adapter.MemberAdapter;
import com.aasoo.freshifybeta.model.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllUserFragment extends Fragment {

    private RecyclerView recyclerView;
    private MemberAdapter memberAdapter;
    private List<UserData> mUser;

    private EditText member_search;
    public AllUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_user, container, false);
        recyclerView = view.findViewById(R.id.member_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUser = new ArrayList<>();
        memberAdapter = new MemberAdapter(getContext(),mUser);
        recyclerView.setAdapter(memberAdapter);

        member_search = view.findViewById(R.id.member_search);
        member_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                readUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        readUsers("");

        return view;

    }


    private void readUsers(String s) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info");
        reference.keepSynced(true);
        Query query = reference.orderByChild("user_account_no")
                .startAt(s)
                .endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserData userData = snapshot.getValue(UserData.class);

                    if (userData != null && !userData.getUser_type().equals("admin") && userData.getApproval_status().equals("approve")) {
                        mUser.add(userData);
                    }
                }

                memberAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
