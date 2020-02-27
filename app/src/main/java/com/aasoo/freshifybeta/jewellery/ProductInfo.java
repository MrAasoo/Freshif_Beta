package com.aasoo.freshifybeta.jewellery;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.aasoo.freshifybeta.OrderActivity;
import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.adapter.SlidePagerAdapter;
import com.aasoo.freshifybeta.model.Product;
import com.aasoo.freshifybeta.model.SlideImage;
import com.aasoo.freshifybeta.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ProductInfo extends AppCompatActivity {

    private Context context = this;
    private RelativeLayout price_layout;
    private TableLayout diamond_info;
    private LinearLayout userLayout, adminLayout;
    private EditText product_quantity;
    private ImageButton minus, plus;
    private TextView quantity_err_msg, add_in_cart, add_in_wishlist, order_now;
    private TextView offer_price,
            price,
            discount,
            name,
            puirty,
            weight,
            diamonds,
            hallmark,
            certificare,
            item_for,
            type,
            product_model_no,
            diamond_weight,
            diamond_puirty,
            diamond_color,
            stock;

    private TextView change_product_offer, show_in_offer;


    private ViewPager slide_image;
    private String[] slideImages;


    private LinearLayout dot_layout;
    private ImageView[] dots;
    private String mCurrentUser;


    int productQuantity ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        final String model_no = getIntent().getStringExtra("product_model_no");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();


        userLayout = findViewById(R.id.for_user_product_info);
        adminLayout = findViewById(R.id.for_admin_product_info);
        product_quantity = findViewById(R.id.product_quantity);
        minus = findViewById(R.id.minus);
        plus = findViewById(R.id.plus);
        quantity_err_msg = findViewById(R.id.quantity_err_msg);
        add_in_cart = findViewById(R.id.add_in_cart);
        add_in_wishlist = findViewById(R.id.add_in_wishlist);
        order_now = findViewById(R.id.order_now);
        offer_price = findViewById(R.id.product_offer_price);
        price = findViewById(R.id.product_price);
        name = findViewById(R.id.product_name);
        puirty = findViewById(R.id.product_puirty);
        weight = findViewById(R.id.product_weight);
        diamonds = findViewById(R.id.product_diamonds);
        hallmark = findViewById(R.id.product_hallmark);
        certificare = findViewById(R.id.product_certificate);
        item_for = findViewById(R.id.product_for);
        type = findViewById(R.id.product_type);
        product_model_no = findViewById(R.id.product_model_no);
        discount = findViewById(R.id.product_discount);
        change_product_offer = findViewById(R.id.change_product_offer);
        show_in_offer = findViewById(R.id.show_in_offer);
        diamond_puirty = findViewById(R.id.diamond_purity);
        diamond_weight = findViewById(R.id.diamond_weight);
        diamond_color = findViewById(R.id.diamond_color);
        price_layout = findViewById(R.id.price_layout);
        diamond_info = findViewById(R.id.diamond_info);
        stock = findViewById(R.id.stock);


        slide_image = findViewById(R.id.slide_image);
        dot_layout = findViewById(R.id.dot_layout);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String user_id = user.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserData userData = dataSnapshot.child(user_id).getValue(UserData.class);
                    String user_type = null;
                    if (userData != null) {
                        user_type = userData.getUser_type();
                        if (user_type.equals("admin") || user_type.equals("member")) {
                            adminLayout.setVisibility(View.VISIBLE);
                        } else {
                            userLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        final DatabaseReference referenceProduct = FirebaseDatabase.getInstance().getReference("product_detail");
        referenceProduct.child(model_no).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null && product.getProduct_model_no().equals(model_no)) {

                    show_slider(model_no, product.getProduct_title_image_url(), referenceProduct);

                    product_model_no.setText(product.getProduct_model_no());
                    if (product.getProduct_price().equals(product.getProduct_offer_price())) {
                        price_layout.setVisibility(View.GONE);
                        offer_price.setText("₹  " + product.getProduct_price());
                    } else {
                        offer_price.setText("₹  " + product.getProduct_offer_price());
                        price.setText("₹  " + product.getProduct_price());
                        discount.setText(product.getProduct_discount() + "% Off");
                        price_layout.setVisibility(View.VISIBLE);
                    }
                    name.setText(product.getProduct_name());
                    puirty.setText(product.getProduct_metal());
                    weight.setText(product.getProduct_weight() + " g.");
                    diamonds.setText(product.getProduct_has_diamond());
                    if (product.getProduct_has_diamond().equals("Yes")) {
                        diamond_info.setVisibility(View.VISIBLE);
                        diamond_puirty.setText(product.getDiamond_purity());
                        diamond_weight.setText(product.getDiamond_weight() + " ct.");
                        diamond_color.setText(product.getDiamond_color());

                    } else {
                        diamond_info.setVisibility(View.GONE);
                    }
                    hallmark.setText(product.getProduct_hallmark());
                    certificare.setText(product.getProduct_certificate());
                    item_for.setText(product.getProduct_for());
                    type.setText(product.getProduct_type());
                    if (product.getProduct_quantity().equals("0")) {
                        stock.setText("Out of stock");
                        stock.setTextColor(Color.rgb(255, 0, 0));
                    } else {
                        if (Integer.parseInt(product.getProduct_quantity()) <= 10) {
                            stock.setText("Only " + product.getProduct_quantity() + " left");
                        } else
                            stock.setText("In stock");
                        stock.setTextColor(Color.rgb(25, 155, 25));
                    }

                    productQuantity = Integer.parseInt(product.getProduct_quantity());

                    if (product.getShow_in_offer().equals("Yes")) {
                        show_in_offer.setText("Remove from daliy offer");
                    } else {
                        show_in_offer.setText("Show in daliy offer");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Quantity(1, "minus");
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Quantity(1, "plus");
            }
        });


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(mCurrentUser);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("my_wishlist").child(model_no).exists()) {
                    add_in_wishlist.setVisibility(View.GONE);
                } else {
                    add_in_wishlist.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.child("my_cart").child(model_no).exists()) {
                    product_quantity.setText(dataSnapshot.child("my_cart").child(model_no).child("product_quantity").getValue().toString());
                } else {
                    product_quantity.setText("0");
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
                String str = s.toString().trim();
                if (str.equals("")) {
                    product_quantity.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        add_in_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q = Integer.parseInt(product_quantity.getText().toString().trim());
                if (q == 0) {
                    quantity_err_msg.setVisibility(View.VISIBLE);
                    quantity_err_msg.setText("Quantity is 0, can not add in cart.\nEnter valid quantity. eg. 10");
                } else if (q > productQuantity){
                    quantity_err_msg.setVisibility(View.VISIBLE);
                    quantity_err_msg.setText("Product may be out of stock or not enough in stock.\nPlease check quantity.");
                }else {
                    quantity_err_msg.setVisibility(View.GONE);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Add in cart")
                            .setMessage("Total quantity : " + product_quantity.getText().toString().trim())
                            .setCancelable(false)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addInCart(product_quantity.getText().toString().trim(), model_no, mCurrentUser);
                        }
                    }).create()
                            .show();

                }
            }
        });

        order_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q = Integer.parseInt(product_quantity.getText().toString().trim());
                if (q == 0) {
                    quantity_err_msg.setVisibility(View.VISIBLE);
                    quantity_err_msg.setText("Quantity is 0, can not place order.\nEnter valid quantity. eg. 10");
                }else if (q > productQuantity){
                    quantity_err_msg.setVisibility(View.VISIBLE);
                    quantity_err_msg.setText("Product may be out of stock or not enough in stock.\nPlease check quantity.");
                }else {
                    quantity_err_msg.setVisibility(View.GONE);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Order now")
                            .setMessage("Total quantity : " + product_quantity.getText().toString().trim())
                            .setCancelable(false)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("Order", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            oderNow(product_quantity.getText().toString().trim(), model_no);
                        }
                    }).create()
                            .show();

                }
            }
        });


        add_in_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInWishlist(model_no, mCurrentUser);
            }
        });

        change_product_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offerIntent = new Intent(context, ProductOfferActivity.class);
                offerIntent.putExtra("product_model_no", model_no);
                startActivity(offerIntent);
            }
        });
        show_in_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_in_offer.setVisibility(View.GONE);
                HashMap<String, Object> map = new HashMap<>();
                switch (show_in_offer.getText().toString()) {
                    case "Remove from daliy offer":
                        map.put("show_in_offer", "No");
                        referenceProduct.child(model_no).updateChildren(map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                Toast.makeText(context, "Product Removed from daliy offers", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case "Show in daliy offer":
                        map.put("show_in_offer", "Yes");
                        referenceProduct.child(model_no).updateChildren(map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                Toast.makeText(context, "Product Added in daliy offers", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
                show_in_offer.setVisibility(View.VISIBLE);

            }
        });
    }

    private void oderNow(String quantity, String model_no) {
        Intent order = new Intent(context, OrderActivity.class);
        order.putExtra("product_model_no", model_no);
        order.putExtra("product_quantity", quantity);
        context.startActivity(order);
    }

    private void show_slider(String model_no, final String product_title_image_url, DatabaseReference referenceProduct) {

        DatabaseReference reference = referenceProduct.child(model_no);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("sub_images").exists()) {
                    final int total = (int) dataSnapshot.child("sub_images").getChildrenCount() + 1;
                    slideImages = new String[total];
                    slideImages[0] = product_title_image_url;
                    int i = 1;
                    for (DataSnapshot snapshot : dataSnapshot.child("sub_images").getChildren()) {
                        SlideImage image_slide = snapshot.getValue(SlideImage.class);
                        if (image_slide != null) {
                            slideImages[i] = image_slide.getImage_url();
                            i++;
                        }
                    }
                    SlidePagerAdapter adapter = new SlidePagerAdapter(context, slideImages);
                    slide_image.setAdapter(adapter);
                    slide_image.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            createDots(position, total);
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                    createDots(0, total);
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new ProductInfo.SlideTimer(total), 10000, 10000);

                } else {
                    slideImages = new String[]{product_title_image_url};
                    SlidePagerAdapter adapter = new SlidePagerAdapter(context, slideImages);
                    slide_image.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createDots(int current_position, int length) {
        if (dot_layout != null) {
            dot_layout.removeAllViews();
        }

        dots = new ImageView[length];

        for (int i = 0; i < length; i++) {
            dots[i] = new ImageView(context);
            if (i == current_position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.selected_dot));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.default_dot));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            dot_layout.addView(dots[i], params);
        }
    }

    private class SlideTimer extends TimerTask {
        private int total;

        private SlideTimer(int total) {
            this.total = total;
        }

        @Override
        public void run() {
            ProductInfo.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (slide_image.getCurrentItem() < total - 1) {
                        slide_image.setCurrentItem(slide_image.getCurrentItem() + 1);
                    } else {
                        slide_image.setCurrentItem(0);
                    }
                }
            });
        }
    }

    private void Quantity(int i, String s) {
        int quantity = Integer.parseInt(product_quantity.getText().toString());
        switch (s) {
            case "minus":
                if (quantity > 0) {
                    quantity_err_msg.setVisibility(View.GONE);
                    quantity = quantity - i;
                } else {
                    quantity_err_msg.setVisibility(View.VISIBLE);
                    quantity_err_msg.setText("Quantity is 0, can not minus.");
                }
                break;
            case "plus":

                quantity_err_msg.setVisibility(View.GONE);
                quantity = quantity + i;
        }
        product_quantity.setText(String.valueOf(quantity));
    }

    private void addInWishlist(String product_model_no, String mCurrentUser) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(mCurrentUser).child("my_wishlist");
        HashMap<String, Object> map = new HashMap<>();
        map.put("product_model_no", product_model_no);
        reference.child(product_model_no).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Product added in My wishlist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addInCart(String quantity, String product_model_no, String mCurrentUser) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Adding in cart");
        dialog.setMessage("Please wait ...");
        dialog.create();
        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(mCurrentUser).child("my_cart");
        HashMap<String, Object> map = new HashMap<>();
        map.put("product_model_no", product_model_no);
        map.put("product_quantity", quantity);
        reference.child(product_model_no).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(context, "Product added in cart", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



}
