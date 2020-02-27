package com.aasoo.freshifybeta.ui.myactivities;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.adapter.UserOderAdapter;
import com.aasoo.freshifybeta.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrdersFragment extends Fragment {


    private RecyclerView recyclerView;
    UserOderAdapter adapter;
    List<Order> orders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orders = new ArrayList<>();
        adapter = new UserOderAdapter(getContext(), orders);
        recyclerView.setAdapter(adapter);
        getMyOrderList();


        return view;
    }

    private void getMyOrderList() {
        String mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference oderReference = FirebaseDatabase.getInstance().getReference("user_info").child(mCurrentUser);
        oderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("my_order").exists()) {
                    orders.clear();
                    for (DataSnapshot snapshot : dataSnapshot.child("my_order").getChildren()) {
                        Order order = snapshot.getValue(Order.class);
                        if (order != null) {
                            orders.add(order);
                        }
                    }
                    Collections.reverse(orders);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Nothing to show here.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
