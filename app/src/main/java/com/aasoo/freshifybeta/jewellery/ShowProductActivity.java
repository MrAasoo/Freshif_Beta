package com.aasoo.freshifybeta.jewellery;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ShowProductActivity extends AppCompatActivity {

    String item_for;
    String item_type;
    String find_from;
    private RecyclerView recyclerView;
    private UserProductAdapter productAdapter;
    private List<Product> mProduct;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);
        Bundle bundle = getIntent().getExtras();
        try {
            item_for = bundle.getString("item_for");
            item_type = bundle.getString("item_type");
            find_from = bundle.getString("find_from");
        } catch (Exception ex) {
            Log.e("ShowProductActivity", "onCreate: " + ex.getMessage(), ex);
        }
        if (find_from.equals("home")) {
            if (item_type.equals("All")) {
                getSupportActionBar().setTitle(item_for + " Jewellery");
            } else {
                getSupportActionBar().setTitle(item_for + " " + item_type);
            }
        } else if (find_from.equals("admin_home")) {
            String title = "Products";

            switch (item_for) {
                case "all":
                    title = "All " + title;
                    break;
                case "out":
                    title = title + " out of Stock";
                    break;
                case "daliy":
                    title = title + " in daliy offer";
                    break;
            }
            getSupportActionBar().setTitle(title);
        }
        recyclerView = findViewById(R.id.show_product_list);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        mProduct = new ArrayList<>();
        productAdapter = new UserProductAdapter(context, mProduct, find_from);
        recyclerView.setAdapter(productAdapter);
        getProductList(find_from, item_for, item_type);
    }

    private void getProductList(final String find_from, final String item_for, final String item_type) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("product_detail");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProduct.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        switch (find_from) {
                            case "home":
                                if (product.getProduct_for().equals(item_for)) {
                                    if (item_type.equals("All")) {
                                        mProduct.add(product);
                                    } else if (product.getProduct_type().equals(item_type)) {
                                        mProduct.add(product);
                                    }
                                }
                                break;
                            case "admin_home":
                                if (item_for.equals("out")) {
                                    if (product.getProduct_quantity().equals("0")) {
                                        mProduct.add(product);
                                    }
                                } else if (item_for.equals("all")) {
                                    mProduct.add(product);
                                }
                                if (item_for.equals("daliy")) {
                                    if (product.getShow_in_offer().equals("Yes")) {
                                        mProduct.add(product);
                                    }
                                }

                                break;
                            case "trending":
                                if (item_type.equals("22kGold")) {
                                    if (product.getProduct_metal().equals("22Kt Yellow Gold") ||
                                            product.getProduct_metal().equals("22Kt Rose Gold") ||
                                            product.getProduct_metal().equals("22Kt Gold")) {
                                        mProduct.add(product);
                                    }
                                } else if(product.getProduct_type().equals(item_type)) {
                                    mProduct.add(product);
                                }
                                break;
                        }

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
