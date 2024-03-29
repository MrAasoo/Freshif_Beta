package com.aasoo.freshifybeta.ui.myactivities;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.adapter.CartAdapter;
import com.aasoo.freshifybeta.adapter.WishlistAdapter;
import com.aasoo.freshifybeta.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCartFragment extends Fragment {



    private RecyclerView recyclerView;
    private List<Product> products;
    private CartAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        String mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        products = new ArrayList<>();
        adapter = new CartAdapter(getContext(), products, mCurrentUser);
        recyclerView.setAdapter(adapter);
        getCartList(mCurrentUser);
        return view;
    }

    private void getCartList(String mCurrentUser) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(mCurrentUser);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("my_cart").exists()) {
                    products.clear();
                    for (DataSnapshot snapshot : dataSnapshot.child("my_cart").getChildren()) {
                        Product mProduct = snapshot.getValue(Product.class);
                        if (mProduct != null) {
                            products.add(mProduct);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(getContext(), "Nothing to show now.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
