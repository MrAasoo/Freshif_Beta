package com.aasoo.freshifybeta.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.adapter.DashboardAdapter;
import com.aasoo.freshifybeta.model.Dashboard;
import com.aasoo.freshifybeta.model.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

private Context context = this;
    TextView note_msg;

    RecyclerView recyclerView;
    List<Dashboard> dashList;
    DashboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        note_msg = findViewById(R.id.note_msg);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);

        final String user_id = getIntent().getStringExtra("user_id");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData userData = dataSnapshot.child(user_id).getValue(UserData.class);
                String user_type = null;
                if (userData != null) {
                    user_type = userData.getUser_type();
                    if (user_type.equals("user")) {
                        note_msg.setVisibility(View.VISIBLE);
                    } else {
                        note_msg.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        dashList = new ArrayList<>();
        adapter = new DashboardAdapter(context,dashList);
        recyclerView.setAdapter(adapter);

        getDashboardList(user_id);
    }

    private void getDashboardList(String user_id) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(user_id).child("my_dashboard");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dashList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Dashboard dash = snapshot.getValue(Dashboard.class);
                    if(dash!=null){
                        dashList.add(dash);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
