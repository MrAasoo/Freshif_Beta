package com.aasoo.freshifybeta.jewellery;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProductOfferActivity extends AppCompatActivity {

    TextView offer_price, offer_err_msg;
    EditText price, product_discount, product_quantity;
    Button btn_save;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_offer);
        getSupportActionBar().setTitle("Change Quantity Offer and price");

        String model_no = getIntent().getStringExtra("product_model_no");

        price = findViewById(R.id.product_price);
        offer_price = findViewById(R.id.product_offer_price);
        product_discount = findViewById(R.id.product_discount);
        product_quantity = findViewById(R.id.product_quantity);
        offer_err_msg = findViewById(R.id.offer_err_msg);
        btn_save = findViewById(R.id.btn_save);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("product_detail").child(model_no);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    price.setText(product.getProduct_price());
                    offer_price.setText(product.getProduct_offer_price());
                    product_discount.setText(product.getProduct_discount());
                    product_quantity.setText(product.getProduct_quantity());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        product_quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().equals("")) {
                    product_quantity.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        product_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateDiscount(price.getText().toString().trim(), s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateDiscount(s.toString(), product_discount.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(context);
                dialog.setTitle("Please wait");
                dialog.setMessage("Updating price and offer ...");
                dialog.create();
                dialog.show();
                HashMap<String, Object> map = new HashMap<>();
                map.put("product_discount", product_discount.getText().toString());
                map.put("product_price", price.getText().toString());
                map.put("product_offer_price", offer_price.getText().toString());
                map.put("product_quantity", product_quantity.getText().toString());
                reference.updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        dialog.dismiss();
                        Toast.makeText(context, "Offer and price updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            }
        });

    }

    private void validateDiscount(String pr, String disp) {
        if (disp.equals("") && pr.equals("")) {
            btn_save.setVisibility(View.GONE);
            offer_err_msg.setVisibility(View.VISIBLE);
            offer_err_msg.setText("Enter product price\nEnter offer value between 0 - 100");
        } else if (disp.equals("")) {
            btn_save.setVisibility(View.GONE);
            offer_err_msg.setVisibility(View.VISIBLE);
            offer_err_msg.setText("Enter offer value between 0 - 100");
        } else if (pr.equals("")) {
            btn_save.setVisibility(View.GONE);
            offer_err_msg.setVisibility(View.VISIBLE);
            offer_err_msg.setText("Enter product price");
        } else {
            int dis = Integer.parseInt(disp);
            int p = Integer.parseInt(pr);
            if (dis < 0 || dis > 100) {
                btn_save.setVisibility(View.GONE);
                offer_err_msg.setVisibility(View.VISIBLE);
                offer_err_msg.setText("Invalid Input\nEnter offer value between 0 - 100");
            } else {
                btn_save.setVisibility(View.VISIBLE);
                offer_err_msg.setVisibility(View.GONE);
                discount(dis, p);
            }
        }
    }

    private void discount(int dis, int p) {
        int ammount = (p * dis) / 100;
        int op = p - ammount;
        offer_price.setText(String.valueOf(op));
    }
}
