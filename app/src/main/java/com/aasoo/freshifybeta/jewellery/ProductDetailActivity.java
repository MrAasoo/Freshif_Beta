package com.aasoo.freshifybeta.jewellery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.aasoo.freshifybeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ProductDetailActivity extends AppCompatActivity {

    private Context context = this;
    String model_no, diamonds = "default", hallmark = "default", weight_diamond, purity_diamond, color_diamond, product_certif;
    private CardView cardView;
    String metal_purity, weight, price;
    private EditText product_weight, product_price, diamond_weight, product_certificate;
    private Button btn_next, btn_cancel;
    private Spinner metal_list, diamond_purity, diamond_color;
    private TextView diamond_err, hallmark_err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        getSupportActionBar().setTitle("Add Product");
        getSupportActionBar().setSubtitle("Product detail");
        model_no = getIntent().getStringExtra("product_model_no");

        cardView = findViewById(R.id.card_view);
        btn_next = findViewById(R.id.btn_next);
        btn_cancel = findViewById(R.id.btn_cancel);

        metal_list = findViewById(R.id.metal_list);
        metal_list.requestFocus();
        diamond_purity = findViewById(R.id.diamond_purity);
        diamond_color = findViewById(R.id.diamond_color);

        product_weight = findViewById(R.id.product_weight);
        product_price = findViewById(R.id.product_price);
        diamond_weight = findViewById(R.id.diamond_weight);
        product_certificate = findViewById(R.id.product_certificate);

        diamond_err = findViewById(R.id.diamond_err);
        hallmark_err = findViewById(R.id.hallmark_err);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discardChanges(model_no);
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metal_purity = metal_list.getSelectedItem().toString();
                weight = product_weight.getText().toString().trim();
                price = product_price.getText().toString().trim();
                product_certif = product_certificate.getText().toString().toUpperCase().trim();
                if (checkProductDetail(weight, price, diamonds, hallmark,product_certif)) {
                    if (diamonds.equals("Yes")) {
                        purity_diamond = diamond_purity.getSelectedItem().toString();
                        color_diamond = diamond_color.getSelectedItem().toString();
                    } else {
                        weight_diamond = "";
                        purity_diamond = "";
                        color_diamond = "";
                    }
                    insertProductDetail(model_no, metal_purity, weight, price, diamonds, hallmark, weight_diamond, purity_diamond, color_diamond, product_certif);
                }
            }
        });


    }

    private void insertProductDetail(final String model_no, String metal_purity, String weight, String price, String diamonds, String hallmark, String weight_diamond, String purity_diamond, String color_diamond, String product_certif) {

        final ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setTitle("Adding product details");
        mDialog.setMessage("Please wait");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.create();
        mDialog.show();
        DatabaseReference refrence = FirebaseDatabase.getInstance().getReference("product_detail").child(model_no);

        HashMap<String, Object> map = new HashMap<>();
        map.put("product_metal", metal_purity);
        map.put("product_weight", weight);
        map.put("product_has_diamond", diamonds);
        map.put("diamond_weight", weight_diamond);
        map.put("diamond_purity", purity_diamond);
        map.put("diamond_color", color_diamond);
        map.put("product_certificate", product_certif);
        map.put("product_hallmark", hallmark);
        map.put("product_price", price);
        map.put("product_offer_price", price);
        map.put("product_discount", "0");
        map.put("product_quantity", "0");
        map.put("show_in_offer", "No");
        refrence.updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                mDialog.dismiss();
                Intent intent = new Intent(context,ProductImageActivity.class);
                intent.putExtra("product_model_no", model_no);
                startActivity(intent);
                finish();
            }
        });
    }


    private boolean checkProductDetail(String weight, String price, String diamonds, String hallmark,String certificate) {
        if (weight.equals("")) {
            product_weight.setError("Enter weight");
            product_weight.requestFocus();
            return false;
        } else {
            product_weight.setText(weight);
        }

        if (diamonds.equals("default")) {
            diamond_err.setVisibility(View.VISIBLE);
            return false;
        } else if (diamonds.equals("Yes")) {
            diamond_err.setVisibility(View.GONE);
            weight_diamond = diamond_weight.getText().toString().trim();
            if (weight_diamond.equals("")) {
                diamond_weight.setError("Enter Diamond weight");
                diamond_weight.requestFocus();
                return false;
            }
        } else
            diamond_err.setVisibility(View.GONE);

        if (product_certif.equals("")) {
            product_certificate.setError("Enter Certificate");
            product_certificate.requestFocus();
            return false;
        }else {
            product_certificate.setText(product_certif);
        }
        if (hallmark.equals("default")) {
            hallmark_err.setVisibility(View.VISIBLE);
            return false;
        } else {
            hallmark_err.setVisibility(View.GONE);
        }

        if (price.equals("")) {
            product_price.setError("Enter price");
            product_price.requestFocus();
            return false;
        } else {
            product_price.setText(price);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        discardChanges(model_no);
    }

    private void discardChanges(final String model_no) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Are you sure ?");
        dialog.setMessage("This will discard your changes and you may lose progress.");
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("product_detail");
                reference.child(model_no).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            finish();
                        }
                    }
                });
            }
        });
        dialog.create();
        dialog.show();
    }


    public void onHallmarkedClick(View view) {
        switch (view.getId()) {
            case R.id.hallmark_yes:
                hallmark = "Yes";
                break;
            case R.id.hallmark_no:
                hallmark = "No";
                break;
        }
    }

    public void onDiamondClick(View view) {
        switch (view.getId()) {
            case R.id.dim_yes:
                diamonds = "Yes";
                cardView.setVisibility(View.VISIBLE);
                break;
            case R.id.dim_no:
                diamonds = "No";
                cardView.setVisibility(View.GONE);
                break;
        }
    }
}
