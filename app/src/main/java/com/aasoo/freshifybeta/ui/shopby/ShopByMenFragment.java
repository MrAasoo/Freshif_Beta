package com.aasoo.freshifybeta.ui.shopby;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.adapter.UserProductAdapter;
import com.aasoo.freshifybeta.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ShopByMenFragment extends Fragment {


    private RecyclerView recyclerView;
    private UserProductAdapter productAdapter;
    private List<Product> mProduct;
    public ShopByMenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_by_men, container, false);
        recyclerView = view.findViewById(R.id.show_product_list);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        mProduct = new ArrayList<>();
        productAdapter = new UserProductAdapter(getContext(), mProduct, "home");
        recyclerView.setAdapter(productAdapter);
        getProductList();
        return view;
    }
    private void getProductList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("product_detail");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProduct.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null && product.getProduct_for().equals("Men's")) {
                        mProduct.add(product);
                    }
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
