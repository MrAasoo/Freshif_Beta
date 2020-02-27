package com.aasoo.freshifybeta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.adapter.AdminOrderAdapter;
import com.aasoo.freshifybeta.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Order> mOrders;
    private AdminOrderAdapter adapter;

    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);
        String user_id = getIntent().getStringExtra("user_id");
        getSupportActionBar().setTitle("All order list");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        mOrders = new ArrayList<>();
        adapter = new AdminOrderAdapter(context, mOrders);
        recyclerView.setAdapter(adapter);
        if (user_id != null) {
            getAllOrderList(user_id);
        } else {
            getAllOrderList("");
        }
    }

    private void getAllOrderList(final String list_type) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("order_list");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOrders.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        if (list_type.equals("")) {
                            mOrders.add(order);
                        } else if (order.getOrder_by_user().equals(list_type)) {
                            mOrders.add(order);
                        }
                    }
                }
                Collections.reverse(mOrders);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
