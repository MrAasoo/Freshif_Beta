package com.aasoo.freshifybeta.ui.myactivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.adapter.NotificationAdapter;
import com.aasoo.freshifybeta.model.MyNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyNotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<MyNotification> notifications;
    private NotificationAdapter adapter;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notification);
        getSupportActionBar().setTitle("My Notifications");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(context,notifications);
        recyclerView.setAdapter(adapter);

        getNotifications();
    }


    private void getNotifications() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(uid).child("my_notification");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifications.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    MyNotification data = snapshot.getValue(MyNotification.class);
                    if(data!=null){
                        notifications.add(data);
                    }
                }
                Collections.reverse(notifications);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
