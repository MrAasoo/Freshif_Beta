package com.aasoo.freshifybeta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aasoo.freshifybeta.model.Order;
import com.aasoo.freshifybeta.model.Product;
import com.aasoo.freshifybeta.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class OrderActivity extends AppCompatActivity {

    final int REQCODEADDRESS = 1111;
    private ImageView product_image;
    private TextView product_model_no, product_price, product_discount, product_offer_price, product_quantity, total_ammount;
    private RelativeLayout product_offer;
    private TextView user_name, user_address, user_mobile;
    private TextView order, cancel, err_txt;
    private Product mProduct;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        final String mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String model_no = getIntent().getStringExtra("product_model_no");
        final String quantity = getIntent().getStringExtra("product_quantity");

        product_image = findViewById(R.id.product_title_image);
        product_model_no = findViewById(R.id.product_model_no);
        product_discount = findViewById(R.id.product_discount);
        product_offer = findViewById(R.id.product_offer);
        product_price = findViewById(R.id.product_price);
        product_offer_price = findViewById(R.id.product_offer_price);
        total_ammount = findViewById(R.id.total_amount);
        product_quantity = findViewById(R.id.quantity_txt);
        order = findViewById(R.id.order);
        cancel = findViewById(R.id.cancel);
        user_name = findViewById(R.id.user_name);
        user_mobile = findViewById(R.id.user_mobile);
        user_address = findViewById(R.id.user_address);
        err_txt = findViewById(R.id.err_msg);

        product_model_no.setText(model_no);
        product_quantity.setText(quantity);
        getProductDetail(model_no, quantity);
        getUserInfo(mCurrentUser);


        user_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent upaddressIntent = new Intent(context, AddressActivity.class);
                upaddressIntent.putExtra("req_type", "order");
                startActivityForResult(upaddressIntent, REQCODEADDRESS);
            }
        });
        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText editText = new EditText(context);
                editText.setText(user_name.getText());
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                alert.setCancelable(false);
                alert.setTitle("Change name");
                alert.setView(editText);

                alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String name = editText.getText().toString();
                        if (!name.trim().equals("")) {
                            user_name.setText(name);
                        } else {
                            Toast.makeText(context, "Invalid name", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        user_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText editText = new EditText(context);
                editText.setText(user_mobile.getText());
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                alert.setCancelable(false);
                alert.setTitle("Change Mobile no.");
                alert.setView(editText);
                alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String number = editText.getText().toString();
                        if (!number.trim().equals("") && number.length() == 10) {
                            user_mobile.setText(number);
                        } else {
                            Toast.makeText(context, "Invalid mobile number", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOrderActivity();
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("product_detail").child(model_no);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mProduct = dataSnapshot.getValue(Product.class);
                        if (mProduct != null) {

                            int productCurrentQuantity = Integer.parseInt(mProduct.getProduct_quantity());
                            if (Integer.parseInt(quantity) > productCurrentQuantity) {
                                Toast.makeText(context, "Product may be out of stock or not enough in stock.\nPlease check quantity.", Toast.LENGTH_SHORT).show();
                            } else if (userInfo()) {
                                String total_price = total_ammount.getText().toString();
                                String product_price = product_offer_price.getText().toString();
                                String userName = user_name.getText().toString();
                                String userAddress = user_address.getText().toString();
                                String userMobile = user_mobile.getText().toString();
                                orderProduct(productCurrentQuantity,mCurrentUser, quantity, model_no, total_price, product_price, userName, userAddress, userMobile);
                            }
                        }

                        
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }


        });


    }

    private void orderProduct(final int proq ,final String mCurrentUser, String quantity, String model_no, String total_price, String product_price, String userName, String userAddress, String userMobile) {
        final ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setTitle("Requesting order");
        mDialog.setMessage("Please wait.\n Do not press back or exit now.");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final String order_no = reference.push().getKey();
        String order_path = "order_list/" + order_no + "/";
        String my_order_path = "user_info/" + mCurrentUser + "/my_order/" + order_no + "/";
        String my_cart_path = "user_info/" + mCurrentUser + "/my_cart/" + model_no;
        String product_path = "product_detail/" + model_no + "/";

        int balanceQuan = proq - Integer.parseInt(quantity);


        HashMap<String, Object> map = new HashMap<>();

        map.put(product_path + "product_quantity",String.valueOf(balanceQuan));


        map.put(order_path + "order_no", order_no);
        map.put(order_path + "order_by_user", mCurrentUser);
        map.put(order_path + "order_model_no", model_no);
        map.put(order_path + "order_quantity", quantity);
        map.put(order_path + "order_item_price", product_price);
        map.put(order_path + "order_status", "new");
        map.put(order_path + "order_date", currentDate());
        map.put(order_path + "order_time", currentTime());
        map.put(order_path + "order_total_price", total_price);
        map.put(order_path + "order_address", userAddress);
        map.put(order_path + "order_name", userName);
        map.put(order_path + "order_mobile", userMobile);
        map.put(order_path + "order_due", total_price);
        map.put(order_path + "order_advance", "0");


        map.put(my_order_path + "order_no", order_no);
        map.put(my_order_path + "order_model_no", model_no);
        map.put(my_order_path + "order_quantity", quantity);
        map.put(my_order_path + "order_item_price", product_price);
        map.put(my_order_path + "order_status", "new");
        map.put(my_order_path + "order_date", currentDate());
        map.put(my_order_path + "order_time", currentTime());
        map.put(my_order_path + "order_total_price", total_price);
        map.put(my_order_path + "order_address", userAddress);
        map.put(my_order_path + "order_name", userName);
        map.put(my_order_path + "order_mobile", userMobile);
        map.put(my_order_path + "order_due", total_price);
        map.put(my_order_path + "order_advance", "0");

        map.put(my_cart_path, null);

        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(OrderActivity.this, "Order successful", Toast.LENGTH_SHORT).show();
                    updateInDashboard(order_no, mCurrentUser, mDialog);
                } else {
                    mDialog.dismiss();
                    Toast.makeText(OrderActivity.this, "Order failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean userInfo() {
        if (user_name.getText().toString().trim().equals("")) {
            err_txt.setVisibility(View.VISIBLE);
            err_txt.setText("Invalid user name.");
            return false;
        }
        if (user_mobile.getText().toString().trim().equals("")) {
            err_txt.setVisibility(View.VISIBLE);
            err_txt.setText("Invalid user mobile no.");
            return false;
        }
        if (user_address.getText().toString().trim().equals("")) {
            err_txt.setVisibility(View.VISIBLE);
            err_txt.setText("Invalid user address.");
            return false;
        }
        return true;
    }

    private void getUserInfo(String mCurrentUser) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(mCurrentUser);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData mData = dataSnapshot.getValue(UserData.class);
                if (mData != null) {
                    user_name.setText(mData.getUser_name());
                    user_mobile.setText(mData.getUser_phone());
                    user_address.setText(mData.getUser_address());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finishOrderActivity();
    }

    private void finishOrderActivity() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Cancel order").setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).create().show();
    }

    private void getProductDetail(String model_no, final String quantity) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("product_detail").child(model_no);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProduct = dataSnapshot.getValue(Product.class);
                if (mProduct != null) {
                    getSupportActionBar().setTitle(mProduct.getProduct_name());
                    Picasso.get().load(mProduct.getProduct_title_image_url()).placeholder(R.drawable.freshify_logo).into(product_image);
                    if (mProduct.getProduct_price().equals(mProduct.getProduct_offer_price())) {
                        product_offer.setVisibility(View.GONE);
                    } else {
                        product_offer.setVisibility(View.VISIBLE);
                        product_price.setText(mProduct.getProduct_price());
                        product_discount.setText(mProduct.getProduct_discount() + "% Off");
                    }
                    product_offer_price.setText(mProduct.getProduct_offer_price());
                    int total = Integer.parseInt(mProduct.getProduct_offer_price()) * Integer.parseInt(quantity);
                    total_ammount.setText(String.valueOf(total));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String currentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }

    private String currentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh.mm a");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQCODEADDRESS && resultCode == RESULT_OK) {
            user_address.setText(data.getStringExtra("address"));
        }
    }


    private void updateInDashboard(final String order_no, String user_id, final ProgressDialog mDialog) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_info").child(user_id);
        final DatabaseReference orderreference = FirebaseDatabase.getInstance().getReference("order_list").child(order_no);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final int sr_no = (int) dataSnapshot.child("my_dashboard").getChildrenCount() + 1;
                final int due = Integer.parseInt(dataSnapshot.child("user_balance").getValue().toString());

                orderreference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String notification = reference.child("my_notification").push().getKey();
                        Order order = dataSnapshot.getValue(Order.class);
                        if (order != null) {
                            String path = "my_dashboard/" + sr_no + "/";


                            HashMap<String, Object> map = new HashMap<>();
                            map.put(path + "dash_sr_no", String.valueOf(sr_no));
                            String date = order.getOrder_date() + "  " + order.getOrder_time();
                            String detail = order.getOrder_quantity() + ", (model) " + order.getOrder_model_no() + ", (price) " + order.getOrder_item_price();
                            map.put(path + "dash_date", date);
                            map.put(path + "dash_type", "Order");
                            map.put(path + "dash_detail", detail);
                            String price = order.getOrder_total_price();
                            map.put(path + "dash_amount", price);
                            int total = due + Integer.parseInt(price);
                            map.put(path + "dash_due_amount", String.valueOf(total));
                            map.put(path + "dash_remark", "New order");

                            //Update user due balance
                            map.put("user_balance", String.valueOf(total));


                            reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mDialog.dismiss();
                                        Toast.makeText(context, "Dashboard updated", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
